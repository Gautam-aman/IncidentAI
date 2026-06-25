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

function drawCanvasFallback(canvas) {
  const context = canvas.getContext("2d");
  if (!context) return;

  const nodes = [
    [0.18, 0.24],
    [0.42, 0.16],
    [0.68, 0.26],
    [0.78, 0.56],
    [0.48, 0.72],
    [0.22, 0.62],
  ];

  function resize() {
    const scale = window.devicePixelRatio || 1;
    canvas.width = Math.floor(window.innerWidth * scale);
    canvas.height = Math.floor(window.innerHeight * scale);
    canvas.style.width = `${window.innerWidth}px`;
    canvas.style.height = `${window.innerHeight}px`;
    context.setTransform(scale, 0, 0, scale, 0, 0);
  }

  function render(time = 0) {
    const width = window.innerWidth;
    const height = window.innerHeight;
    context.clearRect(0, 0, width, height);
    context.lineWidth = 1;

    nodes.forEach(([x, y], index) => {
      const next = nodes[(index + 1) % nodes.length];
      context.strokeStyle = "rgba(92, 220, 210, 0.22)";
      context.beginPath();
      context.moveTo(x * width, y * height);
      context.lineTo(next[0] * width, next[1] * height);
      context.stroke();
    });

    nodes.forEach(([x, y], index) => {
      const pulse = Math.sin(time * 0.002 + index) * 5;
      context.fillStyle = index % 2 ? "rgba(197, 140, 46, 0.72)" : "rgba(54, 184, 199, 0.8)";
      context.beginPath();
      context.arc(x * width, y * height, 9 + pulse, 0, Math.PI * 2);
      context.fill();
    });

    requestAnimationFrame(render);
  }

  resize();
  window.addEventListener("resize", resize);
  requestAnimationFrame(render);
}

async function initTopologyScene() {
  const canvas = $("#topologyScene");
  if (!canvas) return;

  try {
    const THREE = await Promise.race([
      import("https://cdn.jsdelivr.net/npm/three@0.166.1/build/three.module.js"),
      new Promise((_, reject) => {
        window.setTimeout(() => reject(new Error("Three.js import timed out.")), 1200);
      }),
    ]);
    const renderer = new THREE.WebGLRenderer({
      canvas,
      antialias: true,
      alpha: true,
      preserveDrawingBuffer: true,
    });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(42, window.innerWidth / window.innerHeight, 0.1, 100);
    camera.position.set(0, 3.4, 8.5);
    camera.lookAt(0, 0, 0);

    const group = new THREE.Group();
    scene.add(group);

    const ambient = new THREE.AmbientLight(0xffffff, 0.65);
    scene.add(ambient);

    const keyLight = new THREE.PointLight(0x73fff0, 2.2, 18);
    keyLight.position.set(-4, 3, 4);
    scene.add(keyLight);

    const warmLight = new THREE.PointLight(0xffb34d, 1.5, 16);
    warmLight.position.set(4, -1, 2);
    scene.add(warmLight);

    const nodeGeometry = new THREE.IcosahedronGeometry(0.18, 2);
    const tealMaterial = new THREE.MeshStandardMaterial({
      color: 0x45d8cb,
      emissive: 0x0d625a,
      metalness: 0.3,
      roughness: 0.28,
    });
    const amberMaterial = new THREE.MeshStandardMaterial({
      color: 0xd49a3a,
      emissive: 0x70440b,
      metalness: 0.35,
      roughness: 0.32,
    });

    const positions = [
      [-2.7, 0.4, 0.4],
      [-1.1, 1.3, -0.4],
      [0.8, 0.8, 0.2],
      [2.4, 1.6, -0.8],
      [2.8, -0.5, 0.5],
      [0.6, -1.2, -0.2],
      [-1.7, -1.0, 0.8],
    ];

    const nodes = positions.map((position, index) => {
      const mesh = new THREE.Mesh(nodeGeometry, index % 3 === 0 ? amberMaterial : tealMaterial);
      mesh.position.set(...position);
      mesh.userData.baseY = position[1];
      group.add(mesh);
      return mesh;
    });

    const lineMaterial = new THREE.LineBasicMaterial({
      color: 0x88fff1,
      transparent: true,
      opacity: 0.28,
    });

    const connections = [
      [0, 1],
      [1, 2],
      [2, 3],
      [3, 4],
      [4, 5],
      [5, 6],
      [6, 0],
      [1, 5],
      [2, 6],
      [0, 4],
    ];

    connections.forEach(([from, to]) => {
      const points = [nodes[from].position, nodes[to].position];
      const geometry = new THREE.BufferGeometry().setFromPoints(points);
      group.add(new THREE.Line(geometry, lineMaterial));
    });

    const ringMaterial = new THREE.MeshBasicMaterial({
      color: 0x55d9cf,
      transparent: true,
      opacity: 0.12,
      side: THREE.DoubleSide,
    });
    const ring = new THREE.Mesh(new THREE.TorusGeometry(3.7, 0.008, 8, 180), ringMaterial);
    ring.rotation.x = Math.PI / 2.8;
    group.add(ring);

    const particlesGeometry = new THREE.BufferGeometry();
    const particleCount = 180;
    const particlePositions = new Float32Array(particleCount * 3);
    for (let index = 0; index < particleCount; index += 1) {
      particlePositions[index * 3] = (Math.random() - 0.5) * 9;
      particlePositions[index * 3 + 1] = (Math.random() - 0.5) * 5;
      particlePositions[index * 3 + 2] = (Math.random() - 0.5) * 5;
    }
    particlesGeometry.setAttribute("position", new THREE.BufferAttribute(particlePositions, 3));
    const particles = new THREE.Points(
      particlesGeometry,
      new THREE.PointsMaterial({
        color: 0xa7fff7,
        size: 0.025,
        transparent: true,
        opacity: 0.48,
      })
    );
    scene.add(particles);

    function resize() {
      const width = window.innerWidth;
      const height = window.innerHeight;
      renderer.setSize(width, height, false);
      camera.aspect = width / height;
      camera.updateProjectionMatrix();
    }

    function animate(time = 0) {
      const seconds = time * 0.001;
      group.rotation.y = seconds * 0.08;
      group.rotation.x = Math.sin(seconds * 0.18) * 0.08;
      ring.rotation.z = seconds * 0.12;
      particles.rotation.y = seconds * -0.025;

      nodes.forEach((node, index) => {
        node.position.y = node.userData.baseY + Math.sin(seconds * 1.4 + index) * 0.08;
        node.rotation.x += 0.006;
        node.rotation.y += 0.009;
      });

      renderer.render(scene, camera);
      requestAnimationFrame(animate);
    }

    resize();
    window.addEventListener("resize", resize);
    requestAnimationFrame(animate);
  } catch (error) {
    drawCanvasFallback(canvas);
  }
}

initTopologyScene();
