import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  label: { color: "#9BA4C7", fontSize: "12px", marginBottom: "6px", display: "block" },
  input: { width: "100%", padding: "12px 16px", backgroundColor: "#2D3E6B", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box" },
  textarea: { width: "100%", padding: "12px 16px", backgroundColor: "#2D3E6B", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box", minHeight: "100px", resize: "vertical" },
  row: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" },
  submitBtn: { width: "100%", padding: "14px", backgroundColor: "#E85D24", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "15px", fontWeight: "bold", cursor: "pointer", marginBottom: "12px" },
  cancelBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "15px", fontWeight: "bold", cursor: "pointer" },
  success: { backgroundColor: "#1D9E75", color: "#FFFFFF", padding: "12px", borderRadius: "8px", textAlign: "center", marginBottom: "16px", fontWeight: "bold" },
  error: { backgroundColor: "#E24B4A", color: "#FFFFFF", padding: "12px", borderRadius: "8px", textAlign: "center", marginBottom: "16px" },
};

function SubmitIncident() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: "",
    description: "",
    supervisor: "",
    date: new Date().toISOString().split("T")[0],
    time: new Date().toTimeString().slice(0, 5),
  });
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    if (!form.title || !form.description || !form.supervisor || !form.date) {
      setError("Please fill in all required fields");
      return;
    }

    setLoading(true);
    setError("");
    setMessage("");

    try {
      await axios.post(`${BASE_URL}/incidents`, {
        title: form.title,
        description: form.description,
        supervisor: form.supervisor,
        date: form.date,
        time: form.time + ":00",
      }, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });

      setMessage("Incident submitted successfully!");
      setForm({
        title: "",
        description: "",
        supervisor: "",
        date: new Date().toISOString().split("T")[0],
        time: new Date().toTimeString().slice(0, 5),
      });
    } catch (err) {
      setError("Failed to submit incident. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <p style={styles.title}>📋 Submit Incident</p>

        {message && <div style={styles.success}>{message}</div>}
        {error && <div style={styles.error}>{error}</div>}

        <label style={styles.label}>INCIDENT TITLE *</label>
        <input
          style={styles.input}
          name="title"
          placeholder="e.g. Missing Cash"
          value={form.title}
          onChange={handleChange}
        />

        <label style={styles.label}>DESCRIPTION *</label>
        <textarea
          style={styles.textarea}
          name="description"
          placeholder="Describe what happened..."
          value={form.description}
          onChange={handleChange}
        />

        <label style={styles.label}>SUPERVISOR IN CHARGE *</label>
        <input
          style={styles.input}
          name="supervisor"
          placeholder="Supervisor name"
          value={form.supervisor}
          onChange={handleChange}
        />

        <div style={styles.row}>
          <div>
            <label style={styles.label}>DATE *</label>
            <input
              style={styles.input}
              type="date"
              name="date"
              value={form.date}
              onChange={handleChange}
            />
          </div>
          <div>
            <label style={styles.label}>TIME</label>
            <input
              style={styles.input}
              type="time"
              name="time"
              value={form.time}
              onChange={handleChange}
            />
          </div>
        </div>

        <button style={styles.submitBtn} onClick={handleSubmit} disabled={loading}>
          {loading ? "Submitting..." : "SUBMIT"}
        </button>
        <button style={styles.cancelBtn} onClick={() => navigate("/employee/dashboard")}>
          CANCEL
        </button>
      </div>
    </div>
  );
}

export default SubmitIncident;