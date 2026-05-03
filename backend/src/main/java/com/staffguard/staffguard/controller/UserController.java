package com.staffguard.staffguard.controller;

import com.staffguard.staffguard.dto.UserResponseDTO;
import com.staffguard.staffguard.service.UserService;
import com.staffguard.staffguard.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

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
        // Extract email from token
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromToken(token);
        return userService.getMe(email);
    }
}