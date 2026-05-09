import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { getUser, logout, saveAuth, getToken } from "../utils/auth";
import Header from "../utils/Header";

const BASE_URL = "http://localhost:8080/api";

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#1E2A4A", fontFamily: "Inter, system-ui, sans-serif" },
  body: { padding: "24px", maxWidth: "480px", margin: "0 auto" },
  backBtn: { marginBottom: "16px", background: "none", border: "1px solid #4A3DB5", color: "#7F77DD", padding: "8px 16px", borderRadius: "50px", cursor: "pointer", fontSize: "13px" },
  title: { color: "#FFFFFF", fontSize: "18px", fontWeight: "bold", marginBottom: "24px" },
  card: { backgroundColor: "#2D3E6B", borderRadius: "16px", padding: "24px", marginBottom: "24px" },
  avatarWrapper: { position: "relative", width: "88px", marginBottom: "16px" },
  avatar: { width: "88px", height: "88px", borderRadius: "50%", objectFit: "cover", border: "3px solid #4A3DB5" },
  avatarPlaceholder: { width: "88px", height: "88px", borderRadius: "50%", backgroundColor: "#4A3DB5", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "36px", border: "3px solid #7F77DD" },
  changePhotoBtn: { position: "absolute", bottom: 0, right: 0, backgroundColor: "#E85D24", border: "none", borderRadius: "50%", width: "28px", height: "28px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "14px" },
  name: { color: "#FFFFFF", fontSize: "20px", fontWeight: "bold", marginBottom: "4px" },
  role: { color: "#7F77DD", fontSize: "14px", marginBottom: "24px" },
  label: { color: "#9BA4C7", fontSize: "12px", marginBottom: "4px" },
  value: { color: "#FFFFFF", fontSize: "15px", marginBottom: "16px" },
  logoutBtn: { width: "100%", padding: "14px", backgroundColor: "#E24B4A", color: "#FFFFFF", border: "none", borderRadius: "50px", fontSize: "15px", fontWeight: "bold", cursor: "pointer" },
  success: { backgroundColor: "#1D9E75", color: "#FFFFFF", padding: "10px", borderRadius: "8px", textAlign: "center", marginBottom: "12px", fontSize: "13px" },
};

function Profile() {
  const navigate = useNavigate();
  const user = getUser();
  const [profilePic, setProfilePic] = useState(user?.profilePicture || null);
  const [success, setSuccess] = useState("");

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Compress image using canvas before converting to Base64
    const compressImage = (file) => {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onloadend = () => {
          const img = new Image();
          img.onload = () => {
            const canvas = document.createElement("canvas");

            // Max size 400x400
            const MAX_SIZE = 200;
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

            // Compress to JPEG at 70% quality
            const compressed = canvas.toDataURL("image/jpeg", 0.3);
            resolve(compressed);
          };
          img.src = reader.result;
        };
        reader.readAsDataURL(file);
      });
    };

    try {
      const compressed = await compressImage(file);
      const res = await axios.put(`${BASE_URL}/user/profile-picture`,
        { profilePicture: compressed },
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );
      const updatedUser = { ...user, profilePicture: compressed };
      saveAuth(getToken(), updatedUser);
      setProfilePic(compressed);
      setSuccess("Profile picture updated!");
      setTimeout(() => {
        window.location.reload();
      }, 1000);
    } catch (err) {
      console.error(err);
      alert("Failed to update profile picture");
    }
  };

  return (
    <div style={styles.page}>
      <Header />
      <div style={styles.body}>
        <button style={styles.backBtn} onClick={() => navigate("/employee/dashboard")}>
          ← Back
        </button>
        <p style={styles.title}>👤 Profile</p>

        {success && <div style={styles.success}>✅ {success}</div>}

        <div style={styles.card}>
          {/* Avatar with change button */}
          <div style={styles.avatarWrapper}>
            {profilePic ? (
              <img src={profilePic} alt="Profile" style={styles.avatar} />
            ) : (
              <div style={styles.avatarPlaceholder}>👤</div>
            )}
            {/* Hidden file input */}
            <input
              type="file"
              id="profilePicInput"
              accept="image/*"
              style={{ display: "none" }}
              onChange={handleImageChange}
            />
            <button
              style={styles.changePhotoBtn}
              onClick={() => document.getElementById("profilePicInput").click()}>
              📷
            </button>
          </div>

          <p style={styles.name}>{user?.name}</p>
          <p style={styles.role}>{user?.role}</p>

          <p style={styles.label}>EMPLOYEE ID</p>
          <p style={styles.value}>{user?.employeeId || "N/A"}</p>

          <p style={styles.label}>EMAIL</p>
          <p style={styles.value}>{user?.email}</p>

          <p style={styles.label}>ROLE</p>
          <p style={styles.value}>{user?.role}</p>
        </div>

        <button style={styles.logoutBtn} onClick={handleLogout}>
          LOGOUT
        </button>
      </div>
    </div>
  );
}

export default Profile;