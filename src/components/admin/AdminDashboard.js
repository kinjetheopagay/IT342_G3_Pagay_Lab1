import { useNavigate } from "react-router-dom";
import Header from "../utils/Header";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  grid: { display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "16px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "32px 16px", display: "flex", flexDirection: "column", alignItems: "center", cursor: "pointer", transition: "opacity 0.2s" },
  cardIcon: { fontSize: "48px", marginBottom: "12px" },
  cardText: { color: "#FFFFFF", fontSize: "14px", fontWeight: "bold", textAlign: "center" },
};

function AdminDashboard() {
  const navigate = useNavigate();

  const features = [
    { icon: "⚠️", label: "INCIDENTS", path: "/admin/incidents" },
    { icon: "🕐", label: "ATTENDANCE", path: "/admin/attendance" },
    { icon: "📅", label: "SCHEDULING", path: "/admin/scheduling" },
    { icon: "💰", label: "CASH RECORDS", path: "/admin/cash-records" },
    { icon: "👥", label: "EMPLOYEES", path: "/admin/employees" },
];

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <p style={styles.title}>Admin Dashboard</p>
        <div style={styles.grid}>
          {features.map((f) => (
            <div key={f.label} style={styles.card} onClick={() => navigate(f.path)}>
              <span style={styles.cardIcon}>{f.icon}</span>
              <span style={styles.cardText}>{f.label}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;