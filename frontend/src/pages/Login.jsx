import { useEffect, useState } from "react";
import { API_BASE } from "../api";

export default function Login() {
  const [users, setUsers] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    fetch(`${API_BASE}/users`)
      .then(async (r) => {
        if (!r.ok) throw new Error(await r.text());
        return r.json();
      })
      .then((data) => {
        setUsers(data);
        setError("");
      })
      .catch((e) => setError(String(e)));
  }, []);


  
  function handleLogin() {
    const user = users.find((u) => String(u.id) === String(selectedId));
    if (!user) {
      setStatus("");  
      setError("Pick a user first.");
      return;
    }

    localStorage.setItem("userId", String(user.id));
    localStorage.setItem("role", user.role || "");
    localStorage.setItem("name", user.name || "");

    setError("");
    setStatus(`Logged in as ${user.name} (${user.role})`);
  }

  return (
    <div style={{ padding: 16, maxWidth: 520 }}>
      <h1>Login (MVP)</h1>

      {error && <p style={{ color: "tomato" }}>{error}</p>}
      {status && <p style={{ color: "lightgreen" }}>{status}</p>}

      <label style={{ display: "block", marginTop: 12 }}>
        Select user:
      </label>

      <select
        value={selectedId}
        onChange={(e) => setSelectedId(e.target.value)}
        style={{ width: "100%", padding: 10, marginTop: 6 }}
      >
        <option value="">-- choose --</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>
            {u.name} ({u.role}) — id:{u.id}
          </option>
        ))}
      </select>

      <button
        onClick={handleLogin}
        style={{ marginTop: 12, padding: "10px 14px", cursor: "pointer" }}
      >
        Continue
      </button>

      <div style={{ marginTop: 18, opacity: 0.8, fontSize: 14 }}>
        <div>Stored in localStorage:</div>
        <div>userId, role, name</div>
      </div>
    </div>
  );
}
