package com.staffguard.staffguard.user;

import com.staffguard.staffguard.shared.util.JwtUtil;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    public UserResponse getMe(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return userService.getMe(email);
    }

    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/profile-picture")
    public UserResponse updateProfilePicture(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        String email = jwtUtil.getEmailFromToken(authHeader.replace("Bearer ", ""));
        return userService.updateProfilePicture(email, body.get("profilePicture"));
    }
}