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
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "center", gap: "16px" },
  cardLeft: { flex: 1 },
  dateBox: { minWidth: "48px", textAlign: "center", marginRight: "16px" },
  month: { color: "#9BA4C7", fontSize: "11px", fontWeight: "bold" },
  day: { color: "#FFFFFF", fontSize: "22px", fontWeight: "bold" },
  weekday: { color: "#9BA4C7", fontSize: "11px" },
  incidentTitle: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  details: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF", whiteSpace: "nowrap" },
  approveBtn: { padding: "8px 16px", backgroundColor: "#1D9E75", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", cursor: "pointer", marginRight: "8px" },
  rejectBtn: { padding: "8px 16px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", cursor: "pointer" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
};

function IncidentApproval() {
  const navigate = useNavigate();
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/incidents/all`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setIncidents(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id, status) => {
    try {
      await axios.put(`${BASE_URL}/incidents/${id}/status?status=${status}`, {}, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      fetchIncidents();
    } catch (err) {
      console.error(err);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return {
      month: date.toLocaleDateString("en-US", { month: "short" }).toUpperCase(),
      day: date.getDate(),
      weekday: date.toLocaleDateString("en-US", { weekday: "short" }).toUpperCase(),
    };
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/admin/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>⚠️ Incident Approval</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && incidents.length === 0 && (
          <p style={styles.empty}>No incidents found.</p>
        )}

        {incidents.map((incident) => {
          const { month, day, weekday } = formatDate(incident.date);
          return (
            <div key={incident.id} style={styles.card}>
              <div style={styles.dateBox}>
                <p style={styles.month}>{month}</p>
                <p style={styles.day}>{day}</p>
                <p style={styles.weekday}>{weekday}</p>
              </div>

              <div style={styles.cardLeft}>
                <p style={styles.incidentTitle}>{incident.title}</p>
                <p style={styles.details}>By: {incident.employeeName}</p>
                <p style={styles.details}>Filed: {incident.date} | {incident.time}</p>
              </div>

              {incident.status === "PENDING" ? (
                <div>
                  <button style={styles.approveBtn} onClick={() => updateStatus(incident.id, "APPROVED")}>
                    APPROVE
                  </button>
                  <button style={styles.rejectBtn} onClick={() => updateStatus(incident.id, "REJECTED")}>
                    REJECT
                  </button>
                </div>
              ) : (
                <span style={{ ...styles.badge, backgroundColor: statusColors[incident.status] }}>
                  {incident.status}
                </span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default IncidentApproval;