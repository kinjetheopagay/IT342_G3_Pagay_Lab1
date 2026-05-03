# StaffGuard Project Task Checklist

## DONE

### Backend
- User Registration (POST /api/auth/register) - commit hash: e339686
- User Login (POST /api/auth/login) - commit hash: e339686
- Password Encryption (BCrypt) - commit hash: e339686
- JWT Token Generation on Login and Register - commit hash: 3755d08
- JwtUtil created (generate, validate, extract token) - commit hash: 3755d08
- Role field added to User entity - commit hash: 3755d08
- UserResponseDTO updated (returns id, name, email, role, token) - commit hash: 3755d08
- GET /api/user/me endpoint (reads email from JWT) - commit hash: 3755d08
- MySQL database connected (shared with web and mobile) - commit hash: e339686

### Web (ReactJS)
- Login page connected to backend - commit hash: e339686
- Register page connected to backend - commit hash: e339686
- Dashboard/Profile page (protected) - commit hash: e339686

### Mobile (Android Kotlin)
- Project setup (StaffGuardMobile) - commit hash: 3755d08
- Retrofit + ApiClient configured - commit hash: 3755d08
- Login screen (XML + Kotlin) - commit hash: 3755d08
- Register screen (XML + Kotlin) - commit hash: 3755d08
- JWT token saved to SharedPreferences after login - commit hash: 3755d08
- Auto-redirect to Dashboard if already logged in - commit hash: 3755d08
- Dashboard shows user name and role from /api/user/me - commit hash: 3755d08
- Logout clears JWT token and returns to Login - commit hash: 3755d08
- Mobile and Web use same Spring Boot backend API - commit hash: 3755d08
- Mobile and Web share single MySQL database - commit hash: 3755d08

## IN-PROGRESS
- Employee Attendance (Time In / Time Out)
- Submit Incident form
- My Incidents list
- Cash Records list

## TODO
- Admin Dashboard features
- Scheduling module
- Incident approval (Admin)
- Cash record management (Admin)
