package com.example.staffguard;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if(existing.isPresent()) {
            return "Email already registered";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "Registration successful";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if(existing.isEmpty()) {
            return "User not found";
        }
        if(passwordEncoder.matches(user.getPassword(), existing.get().getPassword())) {
            return "Login successful";
        } else {
            return "Invalid password";
        }
    }
}
