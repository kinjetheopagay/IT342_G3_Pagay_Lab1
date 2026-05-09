package com.staffguard.staffguard.util;

import com.staffguard.staffguard.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EmployeeIdGenerator {

    private final UserRepository userRepository;
    private final Random random = new Random();

    public EmployeeIdGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generate() {
        String id;
        do {
            id = String.valueOf(generateId());
        } while (userRepository.existsByEmployeeId(id));
        return id;
    }

    private int generateId() {
        // Get count of existing users to determine batch
        long count = userRepository.count();

        // Base starts at 36000, increments by batch
        int base = 36000 + (int)(count / 3) * 1000;

        // Add random offset within batch range
        int offset = random.nextInt(900) + 50;

        int id = base + offset;

        // Cap at 99999 for 5 digits
        if (id > 99999) id = 90000 + random.nextInt(9999);

        return id;
    }
}