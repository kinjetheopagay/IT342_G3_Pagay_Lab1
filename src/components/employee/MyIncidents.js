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
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "flex-start", cursor: "pointer" },
  cardLeft: { flex: 1 },
  date: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px" },
  incidentTitle: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  filed: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF", whiteSpace: "nowrap" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
  // Modal
  modal: { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, backgroundColor: "rgba(0,0,0,0.8)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 200, padding: "24px" },
  modalCard: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "24px", width: "100%", maxWidth: "480px", maxHeight: "90vh", overflowY: "auto" },
  modalHeader: { display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" },
  modalTitle: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold" },
  closeBtn: { background: "none", border: "none", color: "#9BA4C7", fontSize: "20px", cursor: "pointer" },
  detailLabel: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px", marginTop: "12px" },
  detailValue: { color: "#FFFFFF", fontSize: "14px" },
  proofImage: { width: "100%", borderRadius: "8px", marginTop: "8px" },
  noImage: { color: "#9BA4C7", fontSize: "13px", fontStyle: "italic" },
};

function MyIncidents() {
  const navigate = useNavigate();
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selected, setSelected] = useState(null);

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
    return date.toLocaleDateString("en-US", {
      month: "short", day: "numeric", weekday: "short"
    }).toUpperCase();
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
          <div key={incident.id} style={styles.card} onClick={() => setSelected(incident)}>
            <div style={styles.cardLeft}>
              <p style={styles.date}>{formatDate(incident.date)}</p>
              <p style={styles.incidentTitle}>{incident.title}</p>
              <p style={styles.filed}>
                Filed: {new Date(incident.date).toLocaleDateString()} | {incident.time}
              </p>
            </div>
            <span style={{ ...styles.badge, backgroundColor: statusColors[incident.status] || "#9BA4C7" }}>
              {incident.status}
            </span>
          </div>
        ))}
      </div>

      {/* Incident Detail Modal */}
      {selected && (
        <div style={styles.modal}>
          <div style={styles.modalCard}>
            <div style={styles.modalHeader}>
              <p style={styles.modalTitle}>Incident Details</p>
              <button style={styles.closeBtn} onClick={() => setSelected(null)}>✕</button>
            </div>

            <span style={{
              ...styles.badge,
              backgroundColor: statusColors[selected.status] || "#9BA4C7",
              display: "inline-block",
              marginBottom: "16px"
            }}>
              {selected.status}
            </span>

            <p style={styles.detailLabel}>TITLE</p>
            <p style={styles.detailValue}>{selected.title}</p>

            <p style={styles.detailLabel}>DESCRIPTION</p>
            <p style={styles.detailValue}>{selected.description}</p>

            <p style={styles.detailLabel}>SUPERVISOR</p>
            <p style={styles.detailValue}>{selected.supervisor}</p>

            <p style={styles.detailLabel}>DATE & TIME</p>
            <p style={styles.detailValue}>{selected.date} at {selected.time}</p>

            <p style={styles.detailLabel}>PROOF IMAGE</p>
            {selected.imageUrl ? (
              <img src={selected.imageUrl} alt="Incident proof" style={styles.proofImage} />
            ) : (
              <p style={styles.noImage}>No image uploaded</p>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default MyIncidents;