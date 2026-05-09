import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { isLoggedIn, getRole } from "./components/utils/auth";

// Auth
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";

// Employee
import EmployeeDashboard from "./components/employee/EmployeeDashboard";
import SubmitIncident from "./components/employee/SubmitIncident";
import MyIncidents from "./components/employee/MyIncidents";
import Attendance from "./components/employee/Attendance";
import CashRecords from "./components/employee/CashRecords";
import Profile from "./components/employee/Profile";

// Admin
import AdminDashboard from "./components/admin/AdminDashboard";
import IncidentApproval from "./components/admin/IncidentApproval";
import AdminAttendance from "./components/admin/AdminAttendance";
import AdminCashRecords from "./components/admin/AdminCashRecords";
import Employees from "./components/admin/Employees";
import Scheduling from "./components/admin/Scheduling";

function PrivateRoute({ children, role }) {
  if (!isLoggedIn()) return <Navigate to="/login" />;
  if (role && getRole() !== role) return <Navigate to="/login" />;
  return children;
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Employee Routes */}
        <Route path="/employee/dashboard" element={<PrivateRoute role="EMPLOYEE"><EmployeeDashboard /></PrivateRoute>} />
        <Route path="/employee/submit-incident" element={<PrivateRoute role="EMPLOYEE"><SubmitIncident /></PrivateRoute>} />
        <Route path="/employee/my-incidents" element={<PrivateRoute role="EMPLOYEE"><MyIncidents /></PrivateRoute>} />
        <Route path="/employee/attendance" element={<PrivateRoute role="EMPLOYEE"><Attendance /></PrivateRoute>} />
        <Route path="/employee/cash-records" element={<PrivateRoute role="EMPLOYEE"><CashRecords /></PrivateRoute>} />
        <Route path="/employee/profile" element={<PrivateRoute role="EMPLOYEE"><Profile /></PrivateRoute>} />

        {/* Admin Routes */}
        <Route path="/admin/dashboard" element={<PrivateRoute role="ADMIN"><AdminDashboard /></PrivateRoute>} />
        <Route path="/admin/incidents" element={<PrivateRoute role="ADMIN"><IncidentApproval /></PrivateRoute>} />
        <Route path="/admin/attendance" element={<PrivateRoute role="ADMIN"><AdminAttendance /></PrivateRoute>} />
        <Route path="/admin/cash-records" element={<PrivateRoute role="ADMIN"><AdminCashRecords /></PrivateRoute>} />
        <Route path="/admin/employees" element={<PrivateRoute role="ADMIN"><Employees /></PrivateRoute>} />
        <Route path="/admin/scheduling" element={<PrivateRoute role="ADMIN"><Scheduling /></PrivateRoute>} />
      </Routes>
    </Router>
  );
}

export default App;