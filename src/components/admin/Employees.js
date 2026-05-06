import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", alignItems: "center", gap: "16px" },
  avatar: { width: "48px", height: "48px", borderRadius: "50%", backgroundColor: "#4A3DB5", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "20px", flexShrink: 0 },
  cardLeft: { flex: 1 },
  name: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  email: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "4px 12px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF" },
  deleteBtn: { padding: "8px 16px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", cursor: "pointer", marginLeft: "8px" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
  // Confirm Modal
  modal: { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, backgroundColor: "rgba(0,0,0,0.7)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 200 },
  modalCard: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "32px", width: "100%", maxWidth: "360px", textAlign: "center" },
  modalTitle: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "8px" },
  modalSub: { color: "#9BA4C7", fontSize: "14px", marginBottom: "24px" },
  confirmBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer", marginBottom: "8px" },
  cancelBtn: { width: "100%", padding: "14px", backgroundColor: "#4A3DB5", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer" },
};

function Employees() {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState(null); // for confirm modal

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

  const handleDeleteConfirm = (user) => {
    setSelectedUser(user); // show confirm modal
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`${BASE_URL}/user/${selectedUser.id}`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setSelectedUser(null);
      fetchEmployees(); // refresh list
    } catch (err) {
      console.error(err);
      alert("Failed to delete user");
    }
  };

  return (
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
          <div key={emp.id} style={styles.card}>
            <div style={styles.avatar}>👤</div>
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
            {/* Don't show delete button for ADMIN accounts */}
            {emp.role !== "ADMIN" && (
              <button
                style={styles.deleteBtn}
                onClick={() => handleDeleteConfirm(emp)}>
                DELETE
              </button>
            )}
          </div>
        ))}
      </div>

      {/* Confirm Delete Modal */}
      {selectedUser && (
        <div style={styles.modal}>
          <div style={styles.modalCard}>
            <p style={styles.modalTitle}>Delete User?</p>
            <p style={styles.modalSub}>
              Are you sure you want to delete <strong style={{ color: "#FFFFFF" }}>{selectedUser.name}</strong>? This cannot be undone.
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
    </div>
  );
}

export default Employees;