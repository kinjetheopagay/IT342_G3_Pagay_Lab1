package com.staffguard.staffguard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.staffguard.staffguard.repository.UserRepository;
import com.staffguard.staffguard.model.User;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
public User login(@RequestBody Map<String, String> request) {

    User user = userRepository.findByEmail(request.get("email"))
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (passwordEncoder.matches(request.get("password"), user.getPassword())) {
        return user;
    }

    throw new IllegalArgumentException("Invalid credentials");
}

}
