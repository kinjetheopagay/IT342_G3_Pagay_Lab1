import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "https://staffguard.onrender.com/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "20px", marginBottom: "16px" },
  cardDate: { color: "#7F77DD", fontSize: "13px", fontWeight: "bold", marginBottom: "4px" },
  cardShift: { color: "#FFFFFF", fontSize: "16px", fontWeight: "bold", marginBottom: "12px" },
  supervisorLabel: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px" },
  supervisorName: { color: "#1D9E75", fontSize: "14px", fontWeight: "bold", marginBottom: "12px" },
  employeeList: { display: "flex", flexWrap: "wrap", gap: "8px" },
  employeeBadge: { backgroundColor: "#4A3DB5", color: "#FFFFFF", padding: "4px 12px", borderRadius: "50px", fontSize: "12px" },
  todayBadge: { display: "inline-block", backgroundColor: "#E85D24", color: "#FFFFFF", padding: "4px 12px", borderRadius: "50px", fontSize: "11px", fontWeight: "bold", marginBottom: "8px" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
};

function MySchedule() {
  const navigate = useNavigate();
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchSchedules();
  }, []);

  const fetchSchedules = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/schedules/my`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setSchedules(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString("en-US", {
      weekday: "long", year: "numeric", month: "long", day: "numeric"
    });
  };

  const isToday = (dateStr) => {
    const today = new Date().toISOString().split("T")[0];
    return dateStr === today;
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>My Schedules</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && schedules.length === 0 && (
          <p style={styles.empty}>No schedules assigned yet.</p>
        )}

        {schedules.map((schedule) => (
          <div key={schedule.id} style={styles.card}>

            {/* Today badge */}
            {isToday(schedule.date) && (
              <span style={styles.todayBadge}>TODAY</span>
            )}

            <p style={styles.cardDate}>{formatDate(schedule.date)}</p>
            <p style={styles.cardShift}>
              {schedule.shiftStart} — {schedule.shiftEnd}
            </p>

            <p style={styles.supervisorLabel}>SUPERVISOR</p>
            <p style={styles.supervisorName}>{schedule.supervisorName}</p>

            <p style={styles.supervisorLabel}>EMPLOYEES</p>
            <div style={styles.employeeList}>
              {schedule.employeeNames.map((name, i) => (
                <span key={i} style={styles.employeeBadge}>👤 {name}</span>
              ))}
            </div>

          </div>
        ))}
      </div>
    </div>
  );
}

export default MySchedule;
