package com.staffguard.staffguard.controller;

import com.staffguard.staffguard.dto.UserResponseDTO;
import com.staffguard.staffguard.service.UserService;
import com.staffguard.staffguard.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public UserResponseDTO getMe(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromToken(token);
        return userService.getMe(email);
    }

    @GetMapping("/all")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
public String deleteUser(@PathVariable Long id) {
    return userService.deleteUser(id);
}

@PutMapping("/profile-picture")
public UserResponseDTO updateProfilePicture(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody java.util.Map<String, String> body) {
    String token = authHeader.replace("Bearer ", "");
    String email = jwtUtil.getEmailFromToken(token);
    return userService.updateProfilePicture(email, body.get("profilePicture"));
}
}