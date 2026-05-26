import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "https://staffguard.onrender.com/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", alignItems: "center", gap: "16px", cursor: "pointer" },
  avatar: { width: "48px", height: "48px", borderRadius: "50%", backgroundColor: "#4A3DB5", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "20px", flexShrink: 0 },
  cardLeft: { flex: 1 },
  name: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  email: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "4px 12px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF" },
  deleteBtn: { padding: "8px 16px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", cursor: "pointer", marginLeft: "8px" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
  modal: { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, backgroundColor: "rgba(0,0,0,0.7)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 200, padding: "24px" },
  modalCard: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "32px", width: "100%", maxWidth: "400px" },
  modalTitle: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "8px" },
  modalSub: { color: "#9BA4C7", fontSize: "14px", marginBottom: "24px" },
  confirmBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer", marginBottom: "8px" },
  cancelBtn: { width: "100%", padding: "14px", backgroundColor: "#4A3DB5", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer" },
  detailLabel: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px", marginTop: "16px" },
  detailValue: { color: "#FFFFFF", fontSize: "15px" },
  closeBtn: { background: "none", border: "none", color: "#9BA4C7", fontSize: "20px", cursor: "pointer" },
};

function Employees() {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState(null);
  const [viewUser, setViewUser] = useState(null);

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/user/all`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setEmployees(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteConfirm = (e, user) => {
    e.stopPropagation(); // prevent opening detail modal
    setSelectedUser(user);
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`${BASE_URL}/user/${selectedUser.id}`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setSelectedUser(null);
      fetchEmployees();
    } catch (err) {
      console.error(err);
      alert("Failed to delete user");
    }
  };

  return (
    <>
      <div style={styles.page}>
        <Header />
        <div style={styles.body}>
          <button style={styles.backBtn} onClick={() => navigate("/admin/dashboard")}>
            ← Back
          </button>
          <p style={styles.title}>👥 Employees</p>

          {loading && <p style={styles.empty}>Loading...</p>}
          {!loading && employees.length === 0 && (
            <p style={styles.empty}>No employees found.</p>
          )}

          {employees.map((emp) => (
            <div
              key={emp.id}
              style={styles.card}
              onClick={() => setViewUser(emp)}>

              {emp.profilePicture ? (
                <img
                  src={emp.profilePicture}
                  alt={emp.name}
                  style={{
                    width: "48px", height: "48px", borderRadius: "50%",
                    objectFit: "cover", border: "2px solid #4A3DB5", flexShrink: 0
                  }}/>
              ) : (
                <div style={styles.avatar}>👤</div>
              )}

              <div style={styles.cardLeft}>
                <p style={styles.name}>{emp.name}</p>
                <p style={styles.email}>{emp.email}</p>
              </div>

              <span style={{
                ...styles.badge,
                backgroundColor: emp.role === "ADMIN" ? "#E85D24" : "#4A3DB5"
              }}>
                {emp.role}
              </span>

              {emp.role !== "ADMIN" && (
                <button
                  style={styles.deleteBtn}
                  onClick={(e) => handleDeleteConfirm(e, emp)}>
                  DELETE
                </button>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Employee Detail Modal */}
      {viewUser && (
        <div style={styles.modal}>
          <div style={styles.modalCard}>

            {/* Header row */}
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
              <p style={{ color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", margin: 0 }}>
                Employee Details
              </p>
              <button style={styles.closeBtn} onClick={() => setViewUser(null)}>✕</button>
            </div>

            {/* Avatar */}
            <div style={{ display: "flex", justifyContent: "center", marginBottom: "16px" }}>
              {viewUser.profilePicture ? (
                <img
                  src={viewUser.profilePicture}
                  alt={viewUser.name}
                  style={{
                    width: "88px", height: "88px", borderRadius: "50%",
                    objectFit: "cover", border: "3px solid #4A3DB5"
                  }}/>
              ) : (
                <div style={{
                  width: "88px", height: "88px", borderRadius: "50%",
                  backgroundColor: "#4A3DB5", display: "flex",
                  alignItems: "center", justifyContent: "center", fontSize: "36px"
                }}>
                  👤
                </div>
              )}
            </div>

            {/* Name + Role badge centered */}
            <div style={{ textAlign: "center", marginBottom: "8px" }}>
              <p style={{ color: "#FFFFFF", fontSize: "20px", fontWeight: "bold", margin: "0 0 8px 0" }}>
                {viewUser.name}
              </p>
              <span style={{
                ...styles.badge,
                backgroundColor: viewUser.role === "ADMIN" ? "#E85D24" : "#4A3DB5"
              }}>
                {viewUser.role}
              </span>
            </div>

            {/* Details */}
            <p style={styles.detailLabel}>EMPLOYEE ID</p>
            <p style={styles.detailValue}>{viewUser.employeeId || "N/A"}</p>

            <p style={styles.detailLabel}>EMAIL</p>
            <p style={styles.detailValue}>{viewUser.email}</p>

            <p style={styles.detailLabel}>ROLE</p>
            <p style={styles.detailValue}>{viewUser.role}</p>

            {/* Close button */}
            <button
              style={{ ...styles.cancelBtn, marginTop: "24px" }}
              onClick={() => setViewUser(null)}>
              CLOSE
            </button>
          </div>
        </div>
      )}

      {/* Confirm Delete Modal */}
      {selectedUser && (
        <div style={styles.modal}>
          <div style={{ ...styles.modalCard, textAlign: "center" }}>
            <p style={styles.modalTitle}>Delete User?</p>
            <p style={styles.modalSub}>
              Are you sure you want to delete{" "}
              <strong style={{ color: "#FFFFFF" }}>{selectedUser.name}</strong>?
              This cannot be undone.
            </p>
            <button style={styles.confirmBtn} onClick={handleDelete}>
              YES, DELETE
            </button>
            <button style={styles.cancelBtn} onClick={() => setSelectedUser(null)}>
              CANCEL
            </button>
          </div>
        </div>
      )}
    </>
  );
}

export default Employees;
