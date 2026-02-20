package com.staffguard.staffguard.controller;

import org.springframework.web.bind.annotation.*;
import com.staffguard.staffguard.dto.UserRequestDTO;
import com.staffguard.staffguard.dto.UserResponseDTO;
import com.staffguard.staffguard.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public UserResponseDTO login(@RequestBody UserRequestDTO dto) {
        return userService.login(dto);
    }
}