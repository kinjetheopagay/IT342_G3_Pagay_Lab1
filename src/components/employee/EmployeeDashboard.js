import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  scheduleCard: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "16px" },
  scheduleLabel: { color: "#9BA4C7", fontSize: "12px", margin: 0 },
  scheduleText: { color: "#FFFFFF", fontSize: "14px", margin: "4px 0 0 0" },
  checkBtn: { width: "100%", padding: "18px", border: "none", borderRadius: "12px", fontSize: "16px", fontWeight: "bold", cursor: "pointer", marginBottom: "24px" },
  grid: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px", marginBottom: "24px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "24px", display: "flex", flexDirection: "column", alignItems: "center", cursor: "pointer", border: "2px solid transparent", transition: "border 0.2s" },
  cardIcon: { fontSize: "40px" },
  cardText: { color: "#FFFFFF", fontSize: "13px", fontWeight: "bold", marginTop: "12px", textAlign: "center" },
  toast: { position: "fixed", bottom: "24px", left: "50%", transform: "translateX(-50%)", backgroundColor: "#1D9E75", color: "#FFFFFF", padding: "12px 24px", borderRadius: "8px", fontWeight: "bold" },
};

function EmployeeDashboard() {
  const navigate = useNavigate();
  const [attendance, setAttendance] = useState(null);
  const [toast, setToast] = useState("");

  const authHeader = { headers: { Authorization: `Bearer ${getToken()}` } };

  useEffect(() => {
    fetchTodayAttendance();
  }, []);

  const fetchTodayAttendance = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/attendance/my`, authHeader);
      const today = new Date().toISOString().split("T")[0];
      const todayRecord = res.data.find(a => a.date === today);
      setAttendance(todayRecord || null);
    } catch (err) {
      console.error(err);
    }
  };

  const handleTimeIn = async () => {
    try {
      await axios.post(`${BASE_URL}/attendance/time-in`, {}, authHeader);
      showToast("✅ Time In recorded!");
      fetchTodayAttendance();
    } catch (err) {
      showToast("❌ Already timed in today");
    }
  };

  const handleTimeOut = async () => {
    try {
      await axios.post(`${BASE_URL}/attendance/time-out`, {}, authHeader);
      showToast("✅ Time Out recorded!");
      fetchTodayAttendance();
    } catch (err) {
      showToast("❌ No time-in record found");
    }
  };

  const showToast = (msg) => {
    setToast(msg);
    setTimeout(() => setToast(""), 3000);
  };

  const hasTimedIn = attendance?.timeIn != null;
  const hasTimedOut = attendance?.timeOut != null;

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>

        {/* Schedule Info */}
        <div style={styles.scheduleCard}>
          <p style={styles.scheduleLabel}>TODAY'S SCHEDULE</p>
          <p style={styles.scheduleText}>
            {hasTimedIn
              ? `Time In: ${attendance.timeIn} ${hasTimedOut ? `· Time Out: ${attendance.timeOut}` : ""}`
              : "No time in recorded yet"}
          </p>
        </div>

        {/* Check In / Check Out Button */}
        {!hasTimedIn && (
          <button style={{ ...styles.checkBtn, backgroundColor: "#E85D24" }} onClick={handleTimeIn}>
            CHECK IN — Click here to check in
          </button>
        )}
        {hasTimedIn && !hasTimedOut && (
          <button style={{ ...styles.checkBtn, backgroundColor: "#1D9E75" }} onClick={handleTimeOut}>
            CHECK OUT — Click here to check out
          </button>
        )}
        {hasTimedIn && hasTimedOut && (
          <div style={{ ...styles.scheduleCard, backgroundColor: "#1D9E75", marginBottom: "24px" }}>
            <p style={{ color: "#FFFFFF", textAlign: "center", fontWeight: "bold", margin: 0 }}>
              ✅ Attendance Complete for Today
            </p>
          </div>
        )}

        {/* Feature Grid */}
        <div style={styles.grid}>
          <div style={styles.card} onClick={() => navigate("/employee/submit-incident")}>
            <span style={styles.cardIcon}>⚠️</span>
            <span style={styles.cardText}>SUBMIT INCIDENT</span>
          </div>
          <div style={styles.card} onClick={() => navigate("/employee/my-incidents")}>
            <span style={styles.cardIcon}>📋</span>
            <span style={styles.cardText}>MY INCIDENTS</span>
          </div>
          <div style={styles.card} onClick={() => navigate("/employee/attendance")}>
            <span style={styles.cardIcon}>🕐</span>
            <span style={styles.cardText}>ATTENDANCE</span>
          </div>
          <div style={styles.card} onClick={() => navigate("/employee/cash-records")}>
            <span style={styles.cardIcon}>💰</span>
            <span style={styles.cardText}>CASH RECORDS</span>
          </div>
          <div style={styles.card} onClick={() => navigate("/employee/profile")}>
          <span style={styles.cardIcon}>👤</span>
          <span style={styles.cardText}>PROFILE</span>
         </div>
         </div>

      </div>

      {/* Toast Notification */}
      {toast && <div style={styles.toast}>{toast}</div>}
    </div>
  );
}

export default EmployeeDashboard;