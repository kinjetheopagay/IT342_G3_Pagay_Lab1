import React from "react";
import { getUser, logout } from "./auth";
import { useNavigate } from "react-router-dom";
import staffGuardLogo from "../../assets/staffGuard_logo.png";

function Header() {
  const user = getUser();
  const navigate = useNavigate();
  const isAdmin = user?.role === "ADMIN";
  const [showLogoutConfirm, setShowLogoutConfirm] = React.useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <>
      <div style={{
        backgroundColor: "#8B9FEF",
        padding: "12px 24px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        position: "sticky",
        top: 0,
        zIndex: 100,
      }}>

        {/* Logo */}
        <div
          onClick={() => navigate(isAdmin ? "/admin/dashboard" : "/employee/dashboard")}
          style={{ display: "flex", alignItems: "center", gap: "10px", cursor: "pointer" }}>
          <img
            src={staffGuardLogo}
            alt="StaffGuard"
            style={{ height: "52px", objectFit: "contain" }}/>
        </div>

        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>

          {/* Profile Picture */}
          {!isAdmin && (
            user?.profilePicture ? (
              <img
                src={user.profilePicture}
                alt="Profile"
                style={{
                  width: "36px",
                  height: "36px",
                  borderRadius: "50%",
                  objectFit: "cover",
                  border: "2px solid #FFFFFF",
                  cursor: "pointer"
                }}
                onClick={() => navigate("/employee/profile")}/>
            ) : (
              <div
                style={{
                  width: "36px",
                  height: "36px",
                  borderRadius: "50%",
                  backgroundColor: "#4A3DB5",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: "16px",
                  cursor: "pointer"
                }}
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

          <button onClick={() => setShowLogoutConfirm(true)} style={{
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

      {/* Logout Confirmation Modal */}
      {showLogoutConfirm && (
        <div style={{
          position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: "rgba(0,0,0,0.7)",
          display: "flex", alignItems: "center", justifyContent: "center",
          zIndex: 999, padding: "24px"
        }}>
          <div style={{
            backgroundColor: "#2D3E6B", borderRadius: "16px",
            padding: "32px", width: "100%", maxWidth: "360px", textAlign: "center"
          }}>
            <p style={{ color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "8px" }}>
              Logout?
            </p>
            <p style={{ color: "#9BA4C7", fontSize: "14px", marginBottom: "24px" }}>
              Are you sure you want to logout?
            </p>
            <button
              onClick={handleLogout}
              style={{
                width: "100%", padding: "14px", backgroundColor: "#E24B4A",
                color: "#FFFFFF", border: "none", borderRadius: "50px",
                fontWeight: "bold", cursor: "pointer", marginBottom: "8px"
              }}>
              YES, LOGOUT
            </button>
            <button
              onClick={() => setShowLogoutConfirm(false)}
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
    </>
  );
}

export default Header;
