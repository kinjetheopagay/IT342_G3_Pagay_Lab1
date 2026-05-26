import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken, getUser } from "../utils/auth";
import Header from "../utils/Header";

import submitIncidentImg from "../../assets/emp_submit_incident.png";
import myIncidentsImg from "../../assets/emp_myIncidents.png";
import cashRecordsImg from "../../assets/emp_cashRecords.png";
import attendanceImg from "../../assets/emp_attendance.png";

const BASE_URL = "https://staffguard.onrender.com/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  scheduleCard: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "16px" },
  scheduleLabel: { color: "#9BA4C7", fontSize: "12px", margin: 0 },
  scheduleText: { color: "#FFFFFF", fontSize: "14px", margin: "4px 0 0 0" },
  checkBtn: { width: "100%", padding: "18px", border: "none", borderRadius: "12px", fontSize: "16px", fontWeight: "bold", cursor: "pointer", marginBottom: "24px" },
  grid: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px", marginBottom: "24px" },
  card: {
    backgroundColor: "#2D3E6B",
    borderRadius: "12px",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    cursor: "pointer",
    border: "2px solid transparent",
    transition: "border 0.2s"
  },
  cardImg: { width: "100%", height: "130px", objectFit: "cover", display: "block" },
  cardText: { color: "#FFFFFF", fontSize: "13px", fontWeight: "bold", padding: "10px 0", textAlign: "center" },
  toast: { position: "fixed", bottom: "24px", left: "50%", transform: "translateX(-50%)", backgroundColor: "#1D9E75", color: "#FFFFFF", padding: "12px 24px", borderRadius: "8px", fontWeight: "bold" },
};

function EmployeeDashboard() {
  const navigate = useNavigate();
  const [attendance, setAttendance] = useState(null);
  const [toast, setToast] = useState("");
  const [shiftWarning, setShiftWarning] = useState(false);
  const user = getUser();

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

  const doTimeIn = async () => {
    try {
      await axios.post(`${BASE_URL}/attendance/time-in`, {}, authHeader);
      showToast("Time In recorded!");
      fetchTodayAttendance();
    } catch (err) {
      showToast("Already timed in today");
    }
  };

  const handleTimeIn = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/schedules/my/today`, authHeader);
      const schedule = res.data;

      // No schedule assigned for today — block check in with warning
      if (!schedule) {
        setShiftWarning(true);
        return;
      }

      // Schedule exists — check if current time is within shift hours
      const now = new Date();
      const currentMinutes = now.getHours() * 60 + now.getMinutes();

      const [startH, startM] = schedule.shiftStart.split(":").map(Number);
      const [endH, endM] = schedule.shiftEnd.split(":").map(Number);
      const shiftStartMinutes = startH * 60 + startM;
      const shiftEndMinutes = endH * 60 + endM;

      if (currentMinutes < shiftStartMinutes || currentMinutes > shiftEndMinutes) {
        // Outside shift hours — show warning
        setShiftWarning(true);
        return;
      }

      // Within shift hours — check in directly, no warning
      doTimeIn();

    } catch (err) {
      // API error or no schedule found — show warning, do NOT auto check in
      setShiftWarning(true);
    }
  };

  const handleTimeOut = async () => {
    try {
      await axios.post(`${BASE_URL}/attendance/time-out`, {}, authHeader);
      showToast(" Time Out recorded!");
      fetchTodayAttendance();
    } catch (err) {
      showToast(" No time-in record found");
    }
  };

  const showToast = (msg) => {
    setToast(msg);
    setTimeout(() => setToast(""), 3000);
  };

  const hasTimedIn = attendance?.timeIn != null;
  const hasTimedOut = attendance?.timeOut != null;

  const features = [
    { img: submitIncidentImg, label: "SUBMIT INCIDENT", path: "/employee/submit-incident" },
    { img: myIncidentsImg,    label: "MY INCIDENTS",    path: "/employee/my-incidents" },
    { img: attendanceImg,     label: "ATTENDANCE",      path: "/employee/attendance" },
    { img: cashRecordsImg,    label: "CASH RECORDS",    path: "/employee/cash-records" },
  ];

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

        {/* Feature Grid — image buttons */}
        <div style={styles.grid}>
          {features.map((f) => (
            <div key={f.label} style={styles.card} onClick={() => navigate(f.path)}>
              <img src={f.img} alt={f.label} style={styles.cardImg} />
              <span style={styles.cardText}>{f.label}</span>
            </div>
          ))}
        </div>

        {/* My Schedule — full width button */}
        <div
          onClick={() => navigate("/employee/my-schedule")}
          style={{
            backgroundColor: "#2D3E6B",
            borderRadius: "12px",
            padding: "20px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: "12px",
            cursor: "pointer",
            marginBottom: "24px"
          }}>
          <span style={{ fontSize: "24px" }}></span>
          <span style={{ color: "#FFFFFF", fontSize: "15px", fontWeight: "bold" }}>
            MY SCHEDULE
          </span>
        </div>

        {/* Profile — click profile picture in Header to go to profile */}

      </div>

      {toast && <div style={styles.toast}>{toast}</div>}

      {/* Shift Warning Modal */}
      {shiftWarning && (
        <div style={{
          position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: "rgba(0,0,0,0.7)",
          display: "flex", alignItems: "center", justifyContent: "center",
          zIndex: 200, padding: "24px"
        }}>
          <div style={{
            backgroundColor: "#2D3E6B", borderRadius: "16px",
            padding: "32px", width: "100%", maxWidth: "360px", textAlign: "center"
          }}>
            <p style={{ color: "#E85D24", fontSize: "32px", margin: "0 0 8px 0" }}></p>
            <p style={{ color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "8px" }}>
              Not Your Shift
            </p>
            <p style={{ color: "#9BA4C7", fontSize: "14px", marginBottom: "24px" }}>
              You have no assigned shift today or you are outside your scheduled shift hours. Do you still want to check in?
            </p>
            <button
              onClick={() => { setShiftWarning(false); doTimeIn(); }}
              style={{
                width: "100%", padding: "14px", backgroundColor: "#E85D24",
                color: "#FFFFFF", border: "none", borderRadius: "50px",
                fontWeight: "bold", cursor: "pointer", marginBottom: "8px"
              }}>
              STILL CHECK IN
            </button>
            <button
              onClick={() => setShiftWarning(false)}
              style={{
                width: "100%", padding: "14px", backgroundColor: "#4A3DB5",
                color: "#FFFFFF", border: "none", borderRadius: "50px",
                fontWeight: "bold", cursor: "pointer"
              }}>
              CANCEL
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default EmployeeDashboard;
