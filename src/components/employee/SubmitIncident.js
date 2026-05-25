import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  label: { color: "#9BA4C7", fontSize: "12px", marginBottom: "6px", display: "block" },
  input: { width: "100%", padding: "12px 16px", backgroundColor: "#2D3E6B", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box" },
  textarea: { width: "100%", padding: "12px 16px", backgroundColor: "#2D3E6B", border: "1px solid #4A3DB5", borderRadius: "8px", color: "#FFFFFF", fontSize: "14px", marginBottom: "16px", boxSizing: "border-box", minHeight: "100px", resize: "vertical" },
  row: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" },
  submitBtn: { width: "100%", padding: "14px", backgroundColor: "#E85D24", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "15px", fontWeight: "bold", cursor: "pointer", marginBottom: "12px" },
  cancelBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "15px", fontWeight: "bold", cursor: "pointer" },
  success: { backgroundColor: "#1D9E75", color: "#FFFFFF", padding: "12px", borderRadius: "8px", textAlign: "center", marginBottom: "16px", fontWeight: "bold" },
  error: { backgroundColor: "#E24B4A", color: "#FFFFFF", padding: "12px", borderRadius: "8px", textAlign: "center", marginBottom: "16px" },
  imageUploadBox: { backgroundColor: "#2D3E6B", border: "2px dashed #4A3DB5", borderRadius: "8px", padding: "24px", textAlign: "center", cursor: "pointer", marginBottom: "16px" },
  imageUploadText: { color: "#9BA4C7", fontSize: "13px" },
  previewImage: { width: "100%", borderRadius: "8px", marginBottom: "8px" },
  removeImageBtn: { background: "none", border: "1px solid #E24B4A", color: "#E24B4A", padding: "6px 12px", borderRadius: "50px", cursor: "pointer", fontSize: "12px" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
};

function SubmitIncident() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    title: "",
    description: "",
    supervisor: "",
    date: new Date().toISOString().split("T")[0],
    time: new Date().toTimeString().slice(0, 5),
  });
  const [imageBase64, setImageBase64] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const compressImage = (file) => {
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        const img = new Image();
        img.onload = () => {
          const canvas = document.createElement("canvas");
          const MAX_SIZE = 800;
          let width = img.width;
          let height = img.height;

          if (width > height) {
            if (width > MAX_SIZE) {
              height = (height * MAX_SIZE) / width;
              width = MAX_SIZE;
            }
          } else {
            if (height > MAX_SIZE) {
              width = (width * MAX_SIZE) / height;
              height = MAX_SIZE;
            }
          }

          canvas.width = width;
          canvas.height = height;
          const ctx = canvas.getContext("2d");
          ctx.drawImage(img, 0, 0, width, height);
          const compressed = canvas.toDataURL("image/jpeg", 0.7);
          resolve(compressed);
        };
        img.src = reader.result;
      };
      reader.readAsDataURL(file);
    });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    compressImage(file).then((compressed) => {
      setImageBase64(compressed);
    });
  };

  // ✅ handleSubmit was missing — added here
  const handleSubmit = async () => {
    if (!form.title || !form.description || !form.supervisor || !form.date) {
      setError("Please fill in all required fields");
      return;
    }

    setLoading(true);
    setError("");
    setMessage("");

    try {
      await axios.post(`${BASE_URL}/incidents`, {
        title: form.title,
        description: form.description,
        supervisor: form.supervisor,
        date: form.date,
        time: form.time + ":00",
        imageUrl: imageBase64 || null,
      }, {
        headers: { Authorization: `Bearer ${getToken()}` }
      });

      setMessage("Incident submitted successfully!");
      setForm({
        title: "",
        description: "",
        supervisor: "",
        date: new Date().toISOString().split("T")[0],
        time: new Date().toTimeString().slice(0, 5),
      });
      setImageBase64(null);
    } catch (err) {
      setError("Failed to submit incident. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>Submit Incident</p>

        {message && <div style={styles.success}>{message}</div>}
        {error && <div style={styles.error}>{error}</div>}

        <label style={styles.label}>INCIDENT TITLE *</label>
        <input style={styles.input} name="title" placeholder="e.g. Missing Cash"
          value={form.title} onChange={handleChange} />

        <label style={styles.label}>DESCRIPTION *</label>
        <textarea style={styles.textarea} name="description"
          placeholder="Describe what happened..."
          value={form.description} onChange={handleChange} />

        <label style={styles.label}>SUPERVISOR IN CHARGE *</label>
        <input style={styles.input} name="supervisor" placeholder="Supervisor name"
          value={form.supervisor} onChange={handleChange} />

        <div style={styles.row}>
          <div>
            <label style={styles.label}>DATE *</label>
            <input style={styles.input} type="date" name="date"
              value={form.date} onChange={handleChange} />
          </div>
          <div>
            <label style={styles.label}>TIME</label>
            <input style={styles.input} type="time" name="time"
              value={form.time} onChange={handleChange} />
          </div>
        </div>

        <label style={styles.label}>PROOF IMAGE (optional)</label>
        {imageBase64 ? (
          <div>
            <img src={imageBase64} alt="Preview" style={styles.previewImage} />
            <button style={styles.removeImageBtn} onClick={() => setImageBase64(null)}>
              ✕ Remove Image
            </button>
          </div>
        ) : (
          <div style={styles.imageUploadBox}
            onClick={() => document.getElementById("incidentImageInput").click()}>
            <p style={styles.imageUploadText}>📷 Click to upload proof image</p>
            <p style={{ ...styles.imageUploadText, fontSize: "11px" }}>JPG, PNG supported</p>
          </div>
        )}
        <input type="file" id="incidentImageInput" accept="image/*"
          style={{ display: "none" }} onChange={handleImageChange} />

        <div style={{ marginTop: "16px" }}>
          <button style={styles.submitBtn} onClick={handleSubmit} disabled={loading}>
            {loading ? "Submitting..." : "SUBMIT"}
          </button>
          <button style={styles.cancelBtn} onClick={() => navigate("/employee/dashboard")}>
            CANCEL
          </button>
        </div>
      </div>
    </div>
  );
}

export default SubmitIncident;