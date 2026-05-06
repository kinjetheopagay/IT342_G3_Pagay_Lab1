import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { saveAuth } from "../utils/auth";

const styles = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#1E2A4A",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontFamily: "Inter, system-ui, sans-serif",
  },
  card: {
    backgroundColor: "#2D3E6B",
    borderRadius: "16px",
    padding: "40px",
    width: "100%",
    maxWidth: "420px",
  },
  logo: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "10px",
    marginBottom: "8px",
  },
  logoText: {
    color: "#FFFFFF",
    fontSize: "22px",
    fontWeight: "bold",
  },
  subtitle: {
    color: "#9BA4C7",
    fontSize: "14px",
    textAlign: "center",
    marginBottom: "32px",
  },
  label: {
    color: "#9BA4C7",
    fontSize: "12px",
    marginBottom: "6px",
    display: "block",
  },
  input: {
    width: "100%",
    padding: "12px 16px",
    backgroundColor: "#1E2A4A",
    border: "1px solid #4A3DB5",
    borderRadius: "8px",
    color: "#FFFFFF",
    fontSize: "14px",
    marginBottom: "16px",
    boxSizing: "border-box",
  },
  button: {
    width: "100%",
    padding: "14px",
    backgroundColor: "#4A3DB5",
    color: "#FFFFFF",
    border: "none",
    borderRadius: "50px",
    fontSize: "15px",
    fontWeight: "bold",
    cursor: "pointer",
    marginTop: "8px",
  },
  link: {
    color: "#7F77DD",
    cursor: "pointer",
    textDecoration: "underline",
  },
  footer: {
    color: "#9BA4C7",
    fontSize: "13px",
    textAlign: "center",
    marginTop: "20px",
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
      const res = await axios.post("http://localhost:8080/api/auth/login", {
        email,
        password,
      });

      // Save token and user to localStorage
      saveAuth(res.data.token, res.data);

      // Redirect based on role
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
      <div style={styles.card}>
        <div style={styles.logo}>
          <span style={styles.logoText}>🛡️ StaffGuard</span>
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
            required
          />
          <label style={styles.label}>PASSWORD</label>
          <input
            style={styles.input}
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? "Logging in..." : "LOGIN"}
          </button>
        </form>

        <p style={styles.footer}>
          New User?{" "}
          <span style={styles.link} onClick={() => navigate("/register")}>
            Create an account
          </span>
        </p>
      </div>
    </div>
  );
}

export default Login;