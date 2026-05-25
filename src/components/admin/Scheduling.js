import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "900px", margin: "0 auto" },
  topRow: { display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "24px" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold" },
  backBtn: { background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  addBtn: { padding: "10px 20px", backgroundColor: "#4A3DB5", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "13px", fontWeight: "bold", cursor: "pointer" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "20px", marginBottom: "16px" },
  cardHeader: { display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "12px" },
  cardDate: { color: "#7F77DD", fontSize: "13px", fontWeight: "bold", marginBottom: "4px" },
  cardShift: { color: "#FFFFFF", fontSize: "16px", fontWeight: "bold" },
  deleteBtn: { padding: "6px 14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", cursor: "pointer" },
  supervisorLabel: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px" },
  supervisorName: { color: "#1D9E75", fontSize: "14px", fontWeight: "bold", marginBottom: "12px" },
  employeeList: { display: "flex", flexWrap: "wrap", gap: "8px" },
  employeeBadge: { backgroundColor: "#4A3DB5", color: "#FFFFFF", padding: "4px 12px", borderRadius: "50px", fontSize: "12px" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
  // Modal
  modal: { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, backgroundColor: "rgba(0,0,0,0.8)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 200, overflowY: "auto" },
  modalCard: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "32px", width: "100%", maxWidth: "600px", margin: "24px" },
  modalTitle: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  label: { color: "#9BA4C7", fontSize: "12px", marginBottom: "6px", display: "block" },
  input: { width: "100%", padding: "12px", backgroundColor: "#1E2A4A", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box" },
  select: { width: "100%", padding: "12px", backgroundColor: "#1E2A4A", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box" },
  row: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" },
  employeeRow: { display: "grid", gridTemplateColumns: "1fr auto", gap: "8px", alignItems: "center", marginBottom: "12px" },
  removeBtn: { padding: "8px 12px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "8px", cursor: "pointer", fontSize: "13px" },
  addEmployeeBtn: { padding: "10px 16px", backgroundColor: "#1D9E75", color: "#FFFFFF", border: "none", borderRadius: "8px", cursor: "pointer", fontSize: "13px", fontWeight: "bold", marginBottom: "16px" },
  submitBtn: { width: "100%", padding: "14px", backgroundColor: "#E85D24", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer", marginBottom: "8px" },
  cancelBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer" },
  error: { color: "#E24B4A", fontSize: "13px", marginBottom: "12px" },
};

function Scheduling() {
  const navigate = useNavigate();
  const [schedules, setSchedules] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState("");
  const [form, setForm] = useState({
    supervisorId: "",
    date: "",
    shiftStart: "",
    shiftEnd: "",
    employeeIds: ["", "", ""], // minimum 3
  });

  useEffect(() => {
    fetchSchedules();
    fetchUsers();
  }, []);

  const fetchSchedules = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/schedules/all`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setSchedules(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/user/all`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setUsers(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAddEmployee = () => {
    setForm({ ...form, employeeIds: [...form.employeeIds, ""] });
  };

  const handleRemoveEmployee = (index) => {
    if (form.employeeIds.length <= 3) {
      setError("Minimum 3 employees required");
      return;
    }
    const updated = form.employeeIds.filter((_, i) => i !== index);
    setForm({ ...form, employeeIds: updated });
  };

  const handleEmployeeChange = (index, value) => {
    const updated = [...form.employeeIds];
    updated[index] = value;
    setForm({ ...form, employeeIds: updated });
  };

  const handleSubmit = async () => {
    setError("");

    if (!form.supervisorId || !form.date || !form.shiftStart || !form.shiftEnd) {
      setError("Please fill in all required fields");
      return;
    }

    const filledEmployees = form.employeeIds.filter(id => id !== "");
    if (filledEmployees.length < 3) {
      setError("Please select at least 3 employees");
      return;
    }

    // Check for duplicates
    const unique = new Set(filledEmployees);
    if (unique.size !== filledEmployees.length) {
      setError("Duplicate employees selected");
      return;
    }

    // Check supervisor not in employee list
    if (filledEmployees.includes(form.supervisorId)) {
      setError("Supervisor cannot also be an employee in the same shift");
      return;
    }

    try {
      await axios.post(`${BASE_URL}/schedules`, {
        supervisorId: parseInt(form.supervisorId),
        employeeIds: filledEmployees.map(id => parseInt(id)),
        date: form.date,
        shiftStart: form.shiftStart + ":00",
        shiftEnd: form.shiftEnd + ":00",
      }, { headers: { Authorization: `Bearer ${getToken()}` } });

      setShowModal(false);
      setForm({ supervisorId: "", date: "", shiftStart: "", shiftEnd: "", employeeIds: ["", "", ""] });
      fetchSchedules();
    } catch (err) {
      setError("Failed to create schedule. Please try again.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this schedule?")) return;
    try {
      await axios.delete(`${BASE_URL}/schedules/${id}`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      fetchSchedules();
    } catch (err) {
      console.error(err);
    }
  };

  const employees = users.filter(u => u.role === "EMPLOYEE");
  const supervisors = users.filter(u => u.role === "EMPLOYEE");

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString("en-US", { weekday: "long", year: "numeric", month: "long", day: "numeric" });
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <div style={styles.topRow}>
          <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
            <button style={styles.backBtn} onClick={() => navigate("/admin/dashboard")}>← Back</button>
            <p style={styles.title}>Scheduling</p>
          </div>
          <button style={styles.addBtn} onClick={() => setShowModal(true)}>+ Create Schedule</button>
        </div>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && schedules.length === 0 && (
          <p style={styles.empty}>No schedules yet. Create one!</p>
        )}

        {schedules.map((schedule) => (
          <div key={schedule.id} style={styles.card}>
            <div style={styles.cardHeader}>
              <div>
                <p style={styles.cardDate}>{formatDate(schedule.date)}</p>
                <p style={styles.cardShift}>
                  {schedule.shiftStart} — {schedule.shiftEnd}
                </p>
              </div>
              <button style={styles.deleteBtn} onClick={() => handleDelete(schedule.id)}>
                DELETE
              </button>
            </div>

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

      {/* Create Schedule Modal */}
      {showModal && (
        <div style={styles.modal}>
          <div style={styles.modalCard}>
            <p style={styles.modalTitle}>Create New Schedule</p>

            {error && <p style={styles.error}>{error}</p>}

            <label style={styles.label}>DATE *</label>
            <input
              style={styles.input}
              type="date"
              value={form.date}
              onChange={e => setForm({ ...form, date: e.target.value })}
            />

            <div style={styles.row}>
              <div>
                <label style={styles.label}>SHIFT START *</label>
                <input
                  style={styles.input}
                  type="time"
                  value={form.shiftStart}
                  onChange={e => setForm({ ...form, shiftStart: e.target.value })}
                />
              </div>
              <div>
                <label style={styles.label}>SHIFT END *</label>
                <input
                  style={styles.input}
                  type="time"
                  value={form.shiftEnd}
                  onChange={e => setForm({ ...form, shiftEnd: e.target.value })}
                />
              </div>
            </div>

            <label style={styles.label}>SELECT SUPERVISOR *</label>
            <select
              style={styles.select}
              value={form.supervisorId}
              onChange={e => setForm({ ...form, supervisorId: e.target.value })}>
              <option value="">-- Select Supervisor --</option>
              {supervisors.map(u => (
                <option key={u.id} value={u.id}>{u.name}</option>
              ))}
            </select>

            <label style={styles.label}>SELECT EMPLOYEES * (minimum 3)</label>
            {form.employeeIds.map((empId, index) => (
              <div key={index} style={styles.employeeRow}>
                <select
                  style={{ ...styles.select, marginBottom: 0 }}
                  value={empId}
                  onChange={e => handleEmployeeChange(index, e.target.value)}>
                  <option value="">-- Select Employee --</option>
                  {employees.map(u => (
                    <option key={u.id} value={u.id}>{u.name}</option>
                  ))}
                </select>
                {index >= 3 && (
                  <button style={styles.removeBtn} onClick={() => handleRemoveEmployee(index)}>✕</button>
                )}
              </div>
            ))}

            <button style={styles.addEmployeeBtn} onClick={handleAddEmployee}>
              + ADD EMPLOYEE
            </button>

            <button style={styles.submitBtn} onClick={handleSubmit}>SUBMIT</button>
            <button style={styles.cancelBtn} onClick={() => {
              setShowModal(false);
              setError("");
              setForm({ supervisorId: "", date: "", shiftStart: "", shiftEnd: "", employeeIds: ["", "", ""] });
            }}>CANCEL</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Scheduling;