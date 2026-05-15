package com.staffguard.staffguard.auth;

import com.staffguard.staffguard.shared.exception.EmailAlreadyUsedException;
import com.staffguard.staffguard.shared.exception.InvalidCredentialsException;
import com.staffguard.staffguard.shared.util.EmployeeIdGenerator;
import com.staffguard.staffguard.shared.util.JwtUtil;
import com.staffguard.staffguard.user.User;
import com.staffguard.staffguard.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;
    private final EmployeeIdGenerator employeeIdGenerator;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil,
                       EmployeeIdGenerator employeeIdGenerator) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.employeeIdGenerator = employeeIdGenerator;
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("EMPLOYEE");
        user.setEmployeeId(employeeIdGenerator.generate());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole());

        return new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(),
                saved.getRole(), token, saved.getEmployeeId(), saved.getProfilePicture());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole(), token, user.getEmployeeId(), user.getProfilePicture());
    }
}