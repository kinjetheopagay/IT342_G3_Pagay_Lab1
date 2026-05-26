import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "https://staffguard.onrender.com/api";

const statusColors = {
  FLAT: "#1D9E75",
  SHORT: "#E24B4A",
  OVER: "#E85D24",
};

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "12px", padding: "16px", marginBottom: "12px", display: "flex", justifyContent: "space-between", alignItems: "center" },
  dateBox: { marginRight: "16px", textAlign: "center", minWidth: "48px" },
  month: { color: "#9BA4C7", fontSize: "11px", fontWeight: "bold" },
  day: { color: "#FFFFFF", fontSize: "22px", fontWeight: "bold" },
  weekday: { color: "#9BA4C7", fontSize: "11px" },
  cardLeft: { flex: 1 },
  pos: { color: "#FFFFFF", fontSize: "15px", fontWeight: "bold", marginBottom: "4px" },
  details: { color: "#9BA4C7", fontSize: "12px" },
  badge: { padding: "6px 14px", borderRadius: "50px", fontSize: "12px", fontWeight: "bold", color: "#FFFFFF", textAlign: "center", minWidth: "64px" },
  amount: { color: "#FFFFFF", fontSize: "13px", fontWeight: "bold", marginTop: "4px" },
  empty: { color: "#9BA4C7", textAlign: "center", marginTop: "48px", fontSize: "14px" },
};

function CashRecords() {
  const navigate = useNavigate();
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCashRecords();
  }, []);

  const fetchCashRecords = async () => {
    try {
      const res = await axios.get(`${BASE_URL}/cash-records/my`, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });
      setRecords(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
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
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>Cash Records</p>

        {loading && <p style={styles.empty}>Loading...</p>}
        {!loading && records.length === 0 && (
          <p style={styles.empty}>No cash records yet.</p>
        )}

        {records.map((record) => {
          const { month, day, weekday } = formatDate(record.date);
          const badgeColor = statusColors[record.status] || "#9BA4C7";

          return (
            <div key={record.id} style={styles.card}>
              {/* Date Box */}
              <div style={styles.dateBox}>
                <p style={styles.month}>{month}</p>
                <p style={styles.day}>{day}</p>
                <p style={styles.weekday}>{weekday}</p>
              </div>

              {/* Info */}
              <div style={styles.cardLeft}>
                <p style={styles.pos}>{record.pos}</p>
                <p style={styles.details}>
                  Total Sales: ₱ {record.totalSales?.toLocaleString()}
                </p>
                <p style={styles.details}>
                  Supervisor: {record.supervisorName || "N/A"}
                </p>
              </div>

              {/* Badge + Amount */}
              <div style={{ textAlign: "center" }}>
                <span style={{ ...styles.badge, backgroundColor: badgeColor }}>
                  {record.status}
                </span>
                <p style={styles.amount}>{formatAmount(record.status, record.amount)}</p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default CashRecords;
