import { useNavigate } from "react-router-dom";
import { getUser, logout } from "../utils/auth";
import Header from "../utils/Header";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "24px", marginBottom: "24px" },
  avatar: { width: "72px", height: "72px", borderRadius: "50%", backgroundColor: "#4A3DB5", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "32px", marginBottom: "16px" },
  name: { color: "#FFFFFF", fontSize: "20px", fontWeight: "bold", marginBottom: "4px" },
  role: { color: "#7F77DD", fontSize: "14px", marginBottom: "24px" },
  label: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px" },
  value: { color: "#FFFFFF", fontSize: "15px", marginBottom: "16px" },
  logoutBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "15px", fontWeight: "bold", cursor: "pointer" },
};

function Profile() {
  const navigate = useNavigate();
  const user = getUser();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>👤 Profile</p>

        <div style={styles.card}>
          <div style={styles.avatar}>👤</div>
          <p style={styles.name}>{user?.name}</p>
          <p style={styles.role}>{user?.role}</p>

          <p style={styles.label}>EMAIL</p>
          <p style={styles.value}>{user?.email}</p>

          <p style={styles.label}>EMPLOYEE ID</p>
          <p style={styles.value}>{user?.id}</p>

          <p style={styles.label}>ROLE</p>
          <p style={styles.value}>{user?.role}</p>
        </div>

        <button style={styles.logoutBtn} onClick={handleLogout}>
          LOGOUT
        </button>
      </div>
    </div>
  );
}

export default Profile;