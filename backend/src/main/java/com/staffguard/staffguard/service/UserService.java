package com.staffguard.staffguard.service;

import com.staffguard.staffguard.dto.UserRequestDTO;
import com.staffguard.staffguard.dto.UserResponseDTO;
import com.staffguard.staffguard.exception.EmailAlreadyUsedException;
import com.staffguard.staffguard.exception.InvalidCredentialsException;
import com.staffguard.staffguard.exception.UserNotFoundException;
import com.staffguard.staffguard.model.User;
import com.staffguard.staffguard.repository.UserRepository;
import com.staffguard.staffguard.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public UserResponseDTO register(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException("Email already in use");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("EMPLOYEE"); // default role

        User saved = userRepository.save(user);

        // ✅ Generate JWT token
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole());

        return new UserResponseDTO(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), token);
    }

    public UserResponseDTO login(UserRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // ✅ Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), token);
    }

    public UserResponseDTO getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), null);
    }
}