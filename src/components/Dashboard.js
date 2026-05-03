import { useEffect, useState } from "react";
import axios from "axios";

function Dashboard() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Get the email stored after login
        const email = localStorage.getItem("email");

        if (!email) {
          alert("No user email found. Please login again.");
          window.location.href = "/login";
          return;
        }

        // Call backend with email as query parameter
        const res = await axios.get("http://localhost:8080/api/user/me", {
          params: { email: email }
        });

        setUser(res.data);
      } catch (err) {
        console.error(err);
        alert("Failed to fetch user. Please login again.");
        window.location.href = "/login";
      }
    };

    fetchData();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("email"); // remove stored email
    window.location.href = "/login";   // redirect to login page
  };

  return (
    <div>
      <h2>Dashboard</h2>
      {user ? (
        <div>
          <p>Name: {user.name}</p>
          <p>Email: {user.email}</p>
          <button onClick={handleLogout}>Logout</button>
        </div>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
}

export default Dashboard;
