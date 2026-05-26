import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "https://staffguard.onrender.com/api";

const statusColors = {
  PRESENT: "#1D9E75",
  ABSENT: "#E24B4A",
  REST_DAY: "#9BA4C7",
};

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "center" },
  dateBox: { minWidth: "48px", textAlign: "center", marginRight: "16px" },
  month: { color: "#9BA4C7", fontSize: "11px", fontWeight: "bold" },
  day: { color: "#FFFFFF", fontSize: "22px", fontWeight: "bold" },
  weekday: { color: "#9BA4C7", fontSize: "11px" },
  cardLeft: { flex: 1 },
  employeeName: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  timeText: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
};

function AdminAttendance() {
  const navigate = useNavigate();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAttendance();
  }, []);

  const fetchAttendance = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/attendance/all`, {
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

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/admin/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>Attendance Monitoring</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && records.length === 0 && (
          <p style={styles.empty}>No attendance records found.</p>
        )}

        {records.map((record) => {
          const { month, day, weekday } = formatDate(record.date);
          const badgeColor = statusColors[record.status] || "#9BA4C7";

          return (
            <div key={record.id} style={styles.card}>
              <div style={styles.dateBox}>
                <p style={styles.month}>{month}</p>
                <p style={styles.day}>{day}</p>
                <p style={styles.weekday}>{weekday}</p>
              </div>
              <div style={styles.cardLeft}>
                <p style={styles.employeeName}>{record.employeeName}</p>
                <p style={styles.timeText}>
                  Check In: {record.timeIn?.slice(0, 5) || "N/A"} · Check Out: {record.timeOut?.slice(0, 5) || "N/A"}
                </p>
              </div>
              <span style={{ ...styles.badge, backgroundColor: badgeColor }}>
                {record.status}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default AdminAttendance;
