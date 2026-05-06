// Save token and user info after login
export const saveAuth = (token, user) => {
  localStorage.setItem("token", token);
  localStorage.setItem("user", JSON.stringify(user));
};

// Get JWT token
export const getToken = () => localStorage.getItem("token");

// Get logged in user object
export const getUser = () => {
  const user = localStorage.getItem("user");
  return user ? JSON.parse(user) : null;
};

// Get user role
export const getRole = () => {
  const user = getUser();
  return user ? user.role : null;
};

// Check if logged in
export const isLoggedIn = () => !!getToken();

// Logout
export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
};

// Auth header for API calls
export const authHeader = () => ({
  headers: { Authorization: `Bearer ${getToken()}` }
});