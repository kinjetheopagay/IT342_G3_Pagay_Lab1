package com.staffguard.staffguard.dto;

public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String token;
    private String employeeId;
    private String profilePicture;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String name, String email, String role,
                           String token, String employeeId, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.token = token;
        this.employeeId = employeeId;
        this.profilePicture = profilePicture;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public String getEmployeeId() { return employeeId; }
    public String getProfilePicture() { return profilePicture; }
}