import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const statusColors = {
  FLAT: "#1D9E75",
  SHORT: "#E24B4A",
  OVER: "#E85D24",
};

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "800px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  addBtn: { float: "right", padding: "10px 20px", backgroundColor: "#4A3DB5", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "13px", fontWeight: "bold", cursor: "pointer", marginBottom: "24px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "center" },
  dateBox: { minWidth: "48px", textAlign: "center", marginRight: "16px" },
  month: { color: "#9BA4C7", fontSize: "11px", fontWeight: "bold" },
  day: { color: "#FFFFFF", fontSize: "22px", fontWeight: "bold" },
  weekday: { color: "#9BA4C7", fontSize: "11px" },
  cardLeft: { flex: 1 },
  pos: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  details: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF", textAlign: "center", minWidth: "64px" },
  amount: { color: "#FFFFFF", fontSize: "13px", fontWeight: "bold", marginTop: "4px", textAlign: "center" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
  modal: { position: "fixed", top: 0, left: 0, right: 0, bottom: 0, backgroundColor: "rgba(0,0,0,0.7)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 200 },
  modalCard: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "32px", width: "100%", maxWidth: "400px" },
  modalTitle: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  label: { color: "#9BA4C7", fontSize: "12px", marginBottom: "6px", display: "block" },
  input: { width: "100%", padding: "12px", backgroundColor: "#1E2A4A", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box" },
  select: { width: "100%", padding: "12px", backgroundColor: "#1E2A4A", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box" },
  submitBtn: { width: "100%", padding: "14px", backgroundColor: "#4A3DB5", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer", marginBottom: "8px" },
  cancelModalBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontWeight: "bold", cursor: "pointer" },
};

function AdminCashRecords() {
  const navigate = useNavigate();
  const [records, setRecords] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({ employeeId: "", date: "", pos: "", totalSales: "", amount: "", status: "FLAT" });

  useEffect(() => {
    fetchRecords();
    fetchEmployees();
  }, []);

  const fetchRecords = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/cash-records/all`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setRecords(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchEmployees = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/user/all`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setEmployees(res.data.filter(u => u.role === "EMPLOYEE"));
    } catch (err) {
      console.error(err);
    }
  };

  const handleAdd = async () => {
    try {
      await axios.post(`${BASE_URL}/cash-records/employee/${form.employeeId}`, {
        date: form.date,
        pos: form.pos,
        totalSales: parseFloat(form.totalSales),
        amount: parseFloat(form.amount),
        status: form.status,
      }, { headers: { Authorization: `Bearer ${getToken()}` } });
      setShowModal(false);
      fetchRecords();
    } catch (err) {
      console.error(err);
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return {
      month: date.toLocaleDateString("en-US", { month: "short" }).toUpperCase(),
      day: date.getDate(),
      weekday: date.toLocaleDateString("en-US", { weekday: "short" }).toUpperCase(),
    };
  };

  const formatAmount = (status, amount) => {
    if (status === "FLAT") return "₱ 0";
    if (status === "SHORT") return `- ₱ ${Math.abs(amount)}`;
    if (status === "OVER") return `+ ₱ ${Math.abs(amount)}`;
    return `₱ ${amount}`;
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/admin/dashboard")}>← Back</button>
        <button style={styles.addBtn} onClick={() => setShowModal(true)}>+ Add Cash Record</button>
        <p style={styles.title}>💰 Cash Records</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && records.length === 0 && <p style={styles.empty}>No cash records found.</p>}

        {records.map((record) => {
          const { month, day, weekday } = formatDate(record.date);
          return (
            <div key={record.id} style={styles.card}>
              <div style={styles.dateBox}>
                <p style={styles.month}>{month}</p>
                <p style={styles.day}>{day}</p>
                <p style={styles.weekday}>{weekday}</p>
              </div>
              <div style={styles.cardLeft}>
                <p style={styles.pos}>{record.pos}</p>
                <p style={styles.details}>Total Sales: ₱ {record.totalSales?.toLocaleString()}</p>
                <p style={styles.details}>Employee: {record.employeeName}</p>
                <p style={styles.details}>Supervisor: {record.supervisorName || "N/A"}</p>
              </div>
              <div style={{ textAlign: "center" }}>
                <span style={{ ...styles.badge, backgroundColor: statusColors[record.status] || "#9BA4C7" }}>
                  {record.status}
                </span>
                <p style={styles.amount}>{formatAmount(record.status, record.amount)}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Add Cash Record Modal */}
      {showModal && (
        <div style={styles.modal}>
          <div style={styles.modalCard}>
            <p style={styles.modalTitle}>Add Cash Record</p>

            <label style={styles.label}>EMPLOYEE</label>
            <select style={styles.select} value={form.employeeId} onChange={e => setForm({ ...form, employeeId: e.target.value })}>
              <option value="">Select Employee</option>
              {employees.map(emp => (
                <option key={emp.id} value={emp.id}>{emp.name}</option>
              ))}
            </select>

            <label style={styles.label}>DATE</label>
            <input style={styles.input} type="date" value={form.date} onChange={e => setForm({ ...form, date: e.target.value })} />

            <label style={styles.label}>POS</label>
            <input style={styles.input} placeholder="e.g. POS A" value={form.pos} onChange={e => setForm({ ...form, pos: e.target.value })} />

            <label style={styles.label}>TOTAL SALES</label>
            <input style={styles.input} type="number" placeholder="0.00" value={form.totalSales} onChange={e => setForm({ ...form, totalSales: e.target.value })} />

            <label style={styles.label}>AMOUNT (SHORT/OVER)</label>
            <input style={styles.input} type="number" placeholder="0.00" value={form.amount} onChange={e => setForm({ ...form, amount: e.target.value })} />

            <label style={styles.label}>STATUS</label>
            <select style={styles.select} value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}>
              <option value="FLAT">FLAT</option>
              <option value="SHORT">SHORT</option>
              <option value="OVER">OVER</option>
            </select>

            <button style={styles.submitBtn} onClick={handleAdd}>SUBMIT</button>
            <button style={styles.cancelModalBtn} onClick={() => setShowModal(false)}>CANCEL</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminCashRecords;