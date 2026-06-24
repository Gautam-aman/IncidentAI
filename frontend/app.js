const state = {
  accessToken: localStorage.getItem("incidentai.accessToken") || "",
  refreshToken: localStorage.getItem("incidentai.refreshToken") || "",
  user: JSON.parse(localStorage.getItem("incidentai.user") || "null"),
  currentIncidentId: "",
};

const pageTitles = {
  overview: "Overview",
  auth: "Auth",
  incidents: "Incidents",
  assistant: "AI Assistant",
  search: "Search",
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

updateSession();
