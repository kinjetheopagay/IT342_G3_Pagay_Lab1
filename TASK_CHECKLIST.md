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
- Incident Module (Submit, Get My, Get All, Approve/Reject) - commit hash: 9e17eac
- Attendance Module (Time In, Time Out, Get My, Get All) - commit hash: 9e17eac
- Cash Records Module (Add, Get My, Get All) - commit hash: 9e17eac
- Scheduling Module (Create, Get All, Delete) - commit hash: 8ffb179
- Employee ID auto-generation system - commit hash: 8ffb179
- Profile Picture upload (Base64) - commit hash: 8ffb179
- Incident Image upload (Base64) - commit hash: 8ffb179
- User Management (Get All, Delete) - commit hash: 6666529

### Web (ReactJS)
- Login page with JWT + role-based redirect - commit hash: 3755d08
- Register page with auto-login - commit hash: 3755d08
- Employee Dashboard with Check In/Out - commit hash: 6666529
- Submit Incident with image upload - commit hash: 00c4942
- My Incidents with detail view - commit hash: 00c4942
- Attendance view (monthly list) - commit hash: 6666529
- Cash Records view - commit hash: 6666529
- Profile page with picture upload - commit hash: 00c4942
- Admin Dashboard with 5 features - commit hash: 6666529
- Incident Approval (Approve/Reject + detail view) - commit hash: 00c4942
- Admin Attendance monitoring - commit hash: 6666529
- Admin Cash Records management - commit hash: 00c4942
- Scheduling (Create shift, assign supervisor + employees) - commit hash: 00c4942
- Employees list with delete - commit hash: 6666529

### Mobile (Android Kotlin)
- Project setup (StaffGuardMobile) - commit hash: 3755d08
- Retrofit + ApiClient configured - commit hash: 3755d08
- Login screen connected to backend - commit hash: 3755d08
- Register screen connected to backend - commit hash: 3755d08
- JWT token saved to SharedPreferences - commit hash: 3755d08
- Employee Dashboard with Check In/Out - commit hash: 975d340
- Submit Incident with image upload - commit hash: 975d340
- My Incidents list with detail dialog - commit hash: 975d340
- Attendance view - commit hash: 975d340
- Cash Records view - commit hash: 975d340
- Logout clears token - commit hash: 3755d08
- Mobile and Web share same backend API and MySQL database - commit hash: 975d340

## IN-PROGRESS
- Feature icons (images instead of emojis)
- FRS PDF documentation

## TODO
- Admin mobile features
- Deployment