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
    color: "#9BA4C7",
    fontSize: "14px",
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

function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    // Password security validation
    if (password.length < 8) {
      setError("Password must be at least 8 characters");
      return;
    }
    if (!/[A-Z]/.test(password)) {
      setError("Password must contain at least 1 capital letter");
      return;
    }
    if (!/[0-9]/.test(password)) {
      setError("Password must contain at least 1 number");
      return;
    }
    setLoading(true);
    try {
      const res = await axios.post("http://localhost:8080/api/auth/register", {
        name,
        email,
        password,
      });
      saveAuth(res.data.token, res.data);
      navigate("/employee/dashboard");
    } catch (err) {
      setError("Registration failed. Email may already be used.");
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

        <p style={styles.subtitle}>Register to StaffGuard</p>
        {error && <p style={styles.error}>{error}</p>}

        <form onSubmit={handleSubmit}>
          <label style={styles.label}>NAME</label>
          <input
            style={styles.input}
            placeholder="Enter your full name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required/>

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

          <p style={{ color: "#1E2A4A", fontSize: "11px", marginTop: "-12px", marginBottom: "12px" }}>
            Min. 8 characters, 1 capital letter, 1 number
          </p>

          <label style={styles.label}>CONFIRM PASSWORD</label>
          <input
            style={styles.input}
            type="password"
            placeholder="Confirm your password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required/>

          <button style={styles.button} type="submit" disabled={loading}>
            {loading ? "Creating account..." : "CREATE ACCOUNT"}
          </button>
        </form>
      </div>

      <p style={styles.footer}>
        Already have an account?{" "}
        <span style={styles.link} onClick={() => navigate("/login")}>
          Login
        </span>
      </p>

    </div>
  );
}

export default Register;