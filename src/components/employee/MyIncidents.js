import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const statusColors = {
  APPROVED: "#1D9E75",
  REJECTED: "#E24B4A",
  PENDING: "#E85D24",
};

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "flex-start" },
  cardLeft: { flex: 1 },
  date: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px" },
  incidentTitle: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  filed: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF", whiteSpace: "nowrap" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
};

function MyIncidents() {
  const navigate = useNavigate();
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/incidents/my`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setIncidents(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric", weekday: "short" }).toUpperCase();
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>📋 My Incidents</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && incidents.length === 0 && (
          <p style={styles.empty}>No incidents submitted yet.</p>
        )}

        {incidents.map((incident) => (
          <div key={incident.id} style={styles.card}>
            <div style={styles.cardLeft}>
              <p style={styles.date}>{formatDate(incident.date)}</p>
              <p style={styles.incidentTitle}>{incident.title}</p>
              <p style={styles.filed}>Filed: {new Date(incident.date).toLocaleDateString()} | {incident.time}</p>
            </div>
            <span style={{ ...styles.badge, backgroundColor: statusColors[incident.status] || "#9BA4C7" }}>
              {incident.status}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default MyIncidents;