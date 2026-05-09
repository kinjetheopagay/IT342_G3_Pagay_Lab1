package com.staffguard.staffguard.service;

import com.staffguard.staffguard.dto.UserRequestDTO;
import com.staffguard.staffguard.dto.UserResponseDTO;
import com.staffguard.staffguard.exception.EmailAlreadyUsedException;
import com.staffguard.staffguard.exception.InvalidCredentialsException;
import com.staffguard.staffguard.exception.UserNotFoundException;
import com.staffguard.staffguard.model.User;
import com.staffguard.staffguard.repository.UserRepository;
import com.staffguard.staffguard.util.EmployeeIdGenerator;
import com.staffguard.staffguard.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;
    private final EmployeeIdGenerator employeeIdGenerator;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil,
                       EmployeeIdGenerator employeeIdGenerator) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.employeeIdGenerator = employeeIdGenerator;
    }

    public UserResponseDTO register(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyUsedException("Email already in use");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("EMPLOYEE");

        // ✅ Auto-generate employee ID
        user.setEmployeeId(employeeIdGenerator.generate());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole());
        return new UserResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole(),
                token,
                saved.getEmployeeId(),
                saved.getProfilePicture()
        );
    }

    public UserResponseDTO login(UserRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token,
                user.getEmployeeId(),
                user.getProfilePicture()
        );
    }

    public UserResponseDTO getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                null,
                user.getEmployeeId(),
                user.getProfilePicture()
        );
    }

    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole(),
                        null,
                        u.getEmployeeId(),
                        u.getProfilePicture()
                ))
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateProfilePicture(String email, String profilePicture) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    user.setProfilePicture(profilePicture);
    User saved = userRepository.save(user);
    return new UserResponseDTO(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getRole(),
            null,
            saved.getEmployeeId(),
            saved.getProfilePicture()
    );
}
}