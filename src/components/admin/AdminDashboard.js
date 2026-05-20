import { useNavigate } from "react-router-dom";
import Header from "../utils/Header";

import incidentsImg   from "../../assets/adm_incidents.png";
import attendanceImg  from "../../assets/adm_attendance.png";
import schedulingImg  from "../../assets/adm_scheduling.png";
import cashRecordsImg from "../../assets/adm_cashRecords.png";
import employeesImg   from "../../assets/adm_employees.png";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  grid: { display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "16px" },
  card: {
    backgroundColor: "#2D3E6B",
    borderRadius: "12px",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    cursor: "pointer",
    transition: "opacity 0.2s"
  },
  cardImg: { width: "100%", height: "130px", objectFit: "cover", display: "block" },
  cardText: { color: "#FFFFFF", fontSize: "14px", fontWeight: "bold", padding: "10px 0", textAlign: "center" },
};

function AdminDashboard() {
  const navigate = useNavigate();

  const features = [
    { img: incidentsImg,   label: "INCIDENTS",    path: "/admin/incidents" },
    { img: attendanceImg,  label: "ATTENDANCE",   path: "/admin/attendance" },
    { img: schedulingImg,  label: "SCHEDULING",   path: "/admin/scheduling" },
    { img: cashRecordsImg, label: "CASH RECORDS", path: "/admin/cash-records" },
    { img: employeesImg,   label: "EMPLOYEES",    path: "/admin/employees" },
  ];

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <p style={styles.title}>Admin Dashboard</p>
        <div style={styles.grid}>
          {features.map((f) => (
            <div key={f.label} style={styles.card} onClick={() => navigate(f.path)}>
              <img src={f.img} alt={f.label} style={styles.cardImg} />
              <span style={styles.cardText}>{f.label}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;