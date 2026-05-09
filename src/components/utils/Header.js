import { getUser, logout } from "./auth";
import { useNavigate } from "react-router-dom";

function Header() {
  const user = getUser();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div style={{
      backgroundColor: "#2D3E6B",
      padding: "12px 24px",
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
      position: "sticky",
      top: 0,
      zIndex: 100,
    }}>
      <span
        onClick={() => navigate(isAdmin ? "/admin/dashboard" : "/employee/dashboard")}
        style={{ color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", cursor: "pointer" }}>
        🛡️ StaffGuard
      </span>

      <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
        {/* Profile Picture */}
        {!isAdmin && (
          user?.profilePicture ? (
            <img
              src={user.profilePicture}
              alt="Profile"
              style={{ width: "36px", height: "36px", borderRadius: "50%", objectFit: "cover", border: "2px solid #4A3DB5", cursor: "pointer" }}
              onClick={() => navigate("/employee/profile")}
            />
          ) : (
            <div
              style={{ width: "36px", height: "36px", borderRadius: "50%", backgroundColor: "#4A3DB5", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "16px", cursor: "pointer" }}
              onClick={() => navigate("/employee/profile")}>
              👤
            </div>
          )
        )}

        <span style={{ color: "#FFFFFF", fontSize: "14px", fontWeight: "bold" }}>
          {user?.name}
        </span>
        <span style={{
          backgroundColor: isAdmin ? "#E85D24" : "#4A3DB5",
          color: "#FFFFFF",
          padding: "4px 12px",
          borderRadius: "50px",
          fontSize: "12px",
          fontWeight: "bold",
        }}>
          {user?.role}
        </span>
        <button onClick={handleLogout} style={{
          backgroundColor: "#E24B4A",
          color: "#FFFFFF",
          border: "none",
          padding: "6px 16px",
          borderRadius: "50px",
          fontSize: "12px",
          cursor: "pointer",
          fontWeight: "bold",
        }}>
          LOGOUT
        </button>
      </div>
    </div>
  );
}

export default Header;