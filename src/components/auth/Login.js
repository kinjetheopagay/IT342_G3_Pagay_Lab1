import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { saveAuth } from "../utils/auth";
import staffGuardLogo from "../../assets/staffGuard_logo.png";

const styles = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#1E2A4A",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    fontFamily: "Inter, system-ui, sans-serif",
  },
  
  subtitle: {
    color: "#FFFFFF",
    fontSize: "14px",
    fontWeight: "bold",
    textAlign: "center",
    marginBottom: "24px",
  },
  card: {
    backgroundColor: "#8B9FEF",
    borderRadius: "16px",
    padding: "32px",
    width: "100%",
    maxWidth: "420px",
  },
  label: {
    color: "#1E2A4A",
    fontSize: "12px",
    fontWeight: "bold",
    marginBottom: "6px",
    display: "block",
  },
  input: {
    width: "100%",
    padding: "12px 16px",
    backgroundColor: "#C5CCF0",
    border: "none",
    borderRadius: "50px",
    color: "#1E2A4A",
    fontSize: "14px",
    marginBottom: "16px",
    boxSizing: "border-box",
    outline: "none",
  },
  button: {
    display: "block",
    margin: "8px auto 0 auto",
    padding: "12px 40px",
    backgroundColor: "#1E2A4A",
    color: "#FFFFFF",
    border: "none",
    borderRadius: "50px",
    fontSize: "15px",
    fontWeight: "bold",
    cursor: "pointer",
  },
  footer: {
    color: "#9BA4C7",
    fontSize: "13px",
    textAlign: "center",
    marginTop: "20px",
  },
  link: {
    color: "#7F77DD",
    cursor: "pointer",
    textDecoration: "underline",
  },
  error: {
    color: "#E24B4A",
    fontSize: "13px",
    textAlign: "center",
    marginBottom: "12px",
  },
};

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await axios.post("https://staffguard.onrender.com/api/auth/login", {
        email,
        password,
      });
      saveAuth(res.data.token, res.data);
      if (res.data.role === "ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/employee/dashboard");
      }
    } catch (err) {
      setError("Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.page}>

      {/* Card */}
      <div style={styles.card}>

        {/* Logo inside card */}
        <div style={{ display: "flex", justifyContent: "center", marginBottom: "8px" }}>
          <img src={staffGuardLogo} alt="StaffGuard" style={{ height: "80px", objectFit: "contain" }}/>
        </div>

        <p style={styles.subtitle}>Login to your StaffGuard account</p>
        {error && <p style={styles.error}>{error}</p>}

        <form onSubmit={handleSubmit}>
          <label style={styles.label}>EMAIL</label>
          <input
            style={styles.input}
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required/>

          <label style={styles.label}>PASSWORD</label>
          <input
            style={styles.input}
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required/>

          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? "Logging in..." : "LOGIN"}
          </button>
        </form>
      </div>

      <p style={styles.footer}>
        New User?{" "}
        <span style={styles.link} onClick={() => navigate("/register")}>
          Create an account
        </span>
      </p>

    </div>
  );
}

export default Login;
