import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "https://staffguard.onrender.com/api";

const statusColors = {
  PRESENT: { bg: "#1D9E75", text: "PRESENT" },
  ABSENT: { bg: "#E24B4A", text: "ABSENT" },
  REST_DAY: { bg: "#9BA4C7", text: "REST DAY" },
};

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "center" },
  cardLeft: { flex: 1 },
  dateBox: { marginRight: "16px", textAlign: "center", minWidth: "48px" },
  month: { color: "#9BA4C7", fontSize: "11px", fontWeight: "bold" },
  day: { color: "#FFFFFF", fontSize: "22px", fontWeight: "bold" },
  weekday: { color: "#9BA4C7", fontSize: "11px" },
  statusText: { color: "#FFFFFF", fontSize: "14px", fontWeight: "bold", marginBottom: "4px" },
  timeText: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
};

function Attendance() {
  const navigate = useNavigate();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAttendance();
  }, []);

  const fetchAttendance = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/attendance/my`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setRecords(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
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

  const formatTime = (timeStr) => {
    if (!timeStr) return "N/A";
    return timeStr.slice(0, 5);
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>Attendance</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && records.length === 0 && (
          <p style={styles.empty}>No attendance records yet.</p>
        )}

        {records.map((record) => {
          const { month, day, weekday } = formatDate(record.date);
          const statusInfo = statusColors[record.status] || { bg: "#9BA4C7", text: record.status };

          return (
            <div key={record.id} style={styles.card}>
              {/* Date Box */}
              <div style={styles.dateBox}>
                <p style={styles.month}>{month}</p>
                <p style={styles.day}>{day}</p>
                <p style={styles.weekday}>{weekday}</p>
              </div>

              {/* Info */}
              <div style={styles.cardLeft}>
                <p style={styles.statusText}>{statusInfo.text}</p>
                <p style={styles.timeText}>
                  Check In: {formatTime(record.timeIn)} · Check Out: {formatTime(record.timeOut)}
                </p>
              </div>

              {/* Badge */}
              <span style={{ ...styles.badge, backgroundColor: statusInfo.bg }}>
                {statusInfo.text}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default Attendance;
