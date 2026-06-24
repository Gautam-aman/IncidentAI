const state = {
  accessToken: localStorage.getItem("incidentai.accessToken") || "",
  refreshToken: localStorage.getItem("incidentai.refreshToken") || "",
  user: JSON.parse(localStorage.getItem("incidentai.user") || "null"),
  currentIncidentId: "",
  chatSocket: null,
  chatConnected: false,
  chatIncidentId: "",
  stompSubscriptionId: "incident-room",
};

const pageTitles = {
  overview: "Overview",
  auth: "Auth",
  incidents: "Incidents",
  chat: "Chat",
  assistant: "AI Assistant",
  search: "Search",
  monitoring: "Monitoring",
};

const $ = (selector) => document.querySelector(selector);
const output = $("#output");

function gatewayUrl() {
  return $("#gatewayUrl").value.replace(/\/$/, "");
}

function print(data) {
  output.textContent = typeof data === "string" ? data : JSON.stringify(data, null, 2);
}

function headers(json = true) {
  const base = {};
  if (json) base["Content-Type"] = "application/json";
  if (state.accessToken) base.Authorization = `Bearer ${state.accessToken}`;
  return base;
}

async function api(path, options = {}) {
  const response = await fetch(`${gatewayUrl()}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      ...(options.body instanceof FormData ? headers(false) : headers(true)),
    },
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : {};
  if (!response.ok) {
    throw new Error(data.message || data.detail || response.statusText);
  }
  return data;
}

function saveSession(data) {
  state.accessToken = data.accessToken || "";
  state.refreshToken = data.refreshToken || "";
  state.user = {
    userId: data.userId,
    email: data.email,
    roles: data.roles || [],
  };
  localStorage.setItem("incidentai.accessToken", state.accessToken);
  localStorage.setItem("incidentai.refreshToken", state.refreshToken);
  localStorage.setItem("incidentai.user", JSON.stringify(state.user));
  updateSession();
}

function clearSession() {
  state.accessToken = "";
  state.refreshToken = "";
  state.user = null;
  localStorage.removeItem("incidentai.accessToken");
  localStorage.removeItem("incidentai.refreshToken");
  localStorage.removeItem("incidentai.user");
  updateSession();
}

function updateSession() {
  const signedIn = Boolean(state.user?.email);
  $("#sessionEmail").textContent = signedIn ? state.user.email : "Not signed in";
  $("#authState").textContent = signedIn ? "Signed in" : "Signed out";
  $("#reporterId").value = state.user?.userId || "";
}

function formData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function websocketUrl() {
  return gatewayUrl().replace(/^http/, "ws") + "/ws/native";
}

function stompFrame(command, headersMap = {}, body = "") {
  const headerLines = Object.entries(headersMap).map(([key, value]) => `${key}:${value}`);
  return `${command}\n${headerLines.join("\n")}\n\n${body}\0`;
}

function sendStomp(command, headersMap = {}, body = "") {
  if (!state.chatSocket || state.chatSocket.readyState !== WebSocket.OPEN) {
    throw new Error("Chat is not connected.");
  }
  state.chatSocket.send(stompFrame(command, headersMap, body));
}

function setChatState(connected) {
  state.chatConnected = connected;
  const badge = $("#chatState");
  badge.textContent = connected ? "Connected" : "Disconnected";
  badge.classList.toggle("connected", connected);
}

function addChatMessage(message) {
  const list = $("#chatMessages");
  const empty = list.querySelector(".chat-empty");
  if (empty) empty.remove();

  const item = document.createElement("article");
  item.className = "chat-message";
  const sentAt = new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  item.innerHTML = `
    <header>
      <strong></strong>
      <span>${sentAt}</span>
    </header>
    <p></p>
  `;
  item.querySelector("strong").textContent = message.senderName || "Operator";
  item.querySelector("p").textContent = message.content || "";
  list.appendChild(item);
  list.scrollTop = list.scrollHeight;
}

function resetChatMessages() {
  $("#chatMessages").innerHTML = '<div class="chat-empty">No messages yet.</div>';
}

function handleStompData(data) {
  const frames = data.split("\0").filter(Boolean);
  frames.forEach((frame) => {
    const [headerBlock, body = ""] = frame.split("\n\n");
    const command = headerBlock.split("\n")[0];

    if (command === "CONNECTED") {
      sendStomp("SUBSCRIBE", {
        id: state.stompSubscriptionId,
        destination: `/topic/incidents/${state.chatIncidentId}`,
      });
      setChatState(true);
      print(`Connected to incident chat ${state.chatIncidentId}.`);
      return;
    }

    if (command === "MESSAGE") {
      try {
        addChatMessage(JSON.parse(body));
      } catch {
        addChatMessage({ senderName: "System", content: body });
      }
    }

    if (command === "ERROR") {
      print(body || "Chat connection error.");
    }
  });
}

function disconnectChat() {
  if (state.chatSocket && state.chatSocket.readyState === WebSocket.OPEN) {
    try {
      sendStomp("DISCONNECT");
    } catch {
      // Socket is already closing.
    }
    state.chatSocket.close();
  }
  state.chatSocket = null;
  setChatState(false);
}

async function createIncident(form) {
  const data = formData(form);
  const reporterId = data.reporterId || state.user?.userId;
  if (!reporterId) {
    throw new Error("Sign in first or enter a reporter ID.");
  }

  const incident = await api("/api/incidents", {
    method: "POST",
    body: JSON.stringify({
      title: data.title,
      description: data.description,
      priority: data.priority,
      severity: data.severity,
      reporterId,
    }),
  });

  state.currentIncidentId = incident.id;
  $("#lastIncident").textContent = incident.id || "Created";
  print(incident);
}

document.querySelectorAll(".nav-item").forEach((button) => {
  button.addEventListener("click", () => {
    document.querySelectorAll(".nav-item").forEach((item) => item.classList.remove("active"));
    document.querySelectorAll(".view").forEach((view) => view.classList.remove("active"));
    button.classList.add("active");
    $(`#view-${button.dataset.view}`).classList.add("active");
    $("#pageTitle").textContent = pageTitles[button.dataset.view];
  });
});

$("#clearOutput").addEventListener("click", () => print("Ready."));

$("#registerForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const data = await api("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(formData(event.currentTarget)),
    });
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#loginForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const data = await api("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(formData(event.currentTarget)),
    });
    saveSession(data);
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#logoutBtn").addEventListener("click", async () => {
  if (!state.refreshToken) {
    clearSession();
    print("Signed out locally.");
    return;
  }

  try {
    const data = await api("/api/auth/logout", {
      method: "POST",
      body: JSON.stringify({ refreshToken: state.refreshToken }),
    });
    clearSession();
    print(data);
  } catch (error) {
    clearSession();
    print(error.message);
  }
});

$("#quickIncidentForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    await createIncident(event.currentTarget);
    event.currentTarget.reset();
  } catch (error) {
    print(error.message);
  }
});

$("#incidentForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    await createIncident(event.currentTarget);
  } catch (error) {
    print(error.message);
  }
});

$("#lookupForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const { incidentId } = formData(event.currentTarget);
    state.currentIncidentId = incidentId;
    const data = await api(`/api/incidents/${incidentId}`);
    $("#lastIncident").textContent = data.id || incidentId;
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#statusForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    if (!state.currentIncidentId) throw new Error("Load an incident first.");
    const { status } = formData(event.currentTarget);
    const data = await api(`/api/incidents/${state.currentIncidentId}/status`, {
      method: "PUT",
      body: JSON.stringify({
        status,
        performedBy: state.user?.userId || "",
      }),
    });
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#uploadForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const body = new FormData(event.currentTarget);
    const data = await api("/api/upload", {
      method: "POST",
      body,
    });
    $("#ragState").textContent = `${data.indexed_chunks || 0} chunks`;
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#askForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const { question } = formData(event.currentTarget);
    const data = await api("/api/ask", {
      method: "POST",
      body: JSON.stringify({ question, limit: 5 }),
    });
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#searchForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  try {
    const { query } = formData(event.currentTarget);
    const data = await api(`/api/search?query=${encodeURIComponent(query)}`);
    print(data);
  } catch (error) {
    print(error.message);
  }
});

$("#chatConnectForm").addEventListener("submit", (event) => {
  event.preventDefault();
  const { incidentId } = formData(event.currentTarget);
  disconnectChat();

  state.chatIncidentId = incidentId;
  state.currentIncidentId = incidentId;
  state.chatSocket = new WebSocket(websocketUrl());

  state.chatSocket.addEventListener("open", () => {
    sendStomp("CONNECT", {
      "accept-version": "1.2",
      host: window.location.host,
    });
  });

  state.chatSocket.addEventListener("message", (message) => handleStompData(message.data));
  state.chatSocket.addEventListener("close", () => setChatState(false));
  state.chatSocket.addEventListener("error", () => print("Chat connection failed. Check that gateway and chat-service are running."));
});

$("#disconnectChatBtn").addEventListener("click", () => {
  disconnectChat();
  print("Chat disconnected.");
});

$("#clearChatBtn").addEventListener("click", resetChatMessages);

$("#chatMessageForm").addEventListener("submit", (event) => {
  event.preventDefault();
  try {
    if (!state.chatConnected) throw new Error("Connect to an incident room first.");
    const data = formData(event.currentTarget);
    const message = {
      incidentId: state.chatIncidentId,
      senderId: state.user?.userId || "anonymous",
      senderName: data.senderName || state.user?.email || "Operator",
      content: data.content,
      type: "CHAT",
    };

    sendStomp("SEND", {
      destination: "/app/chat.send",
      "content-type": "application/json",
    }, JSON.stringify(message));
    event.currentTarget.reset();
    $("#chatSenderName").value = state.user?.email || "";
  } catch (error) {
    print(error.message);
  }
});

$("#checkHealthBtn").addEventListener("click", async () => {
  const services = [
    ["Gateway", `${gatewayUrl()}/actuator/health`],
    ["Auth", `${gatewayUrl()}/monitor/auth/health`],
    ["Ticket", `${gatewayUrl()}/monitor/ticket/health`],
    ["Notification", `${gatewayUrl()}/monitor/notification/health`],
    ["Search", `${gatewayUrl()}/monitor/search/health`],
    ["Chat", `${gatewayUrl()}/monitor/chat/health`],
    ["RAG", `${gatewayUrl()}/monitor/rag/health`],
  ];

  const results = await Promise.allSettled(
    services.map(async ([name, url]) => {
      const response = await fetch(url);
      return [name, response.ok ? "UP" : "DOWN"];
    })
  );

  $("#healthGrid").innerHTML = results.map((result, index) => {
    const name = services[index][0];
    const status = result.status === "fulfilled" ? result.value[1] : "DOWN";
    return `<div class="health-card ${status === "UP" ? "up" : "down"}">${name}<span>${status}</span></div>`;
  }).join("");
});

updateSession();
resetChatMessages();
