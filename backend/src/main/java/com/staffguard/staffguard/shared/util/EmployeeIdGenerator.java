package com.staffguard.staffguard.shared.util;

import com.staffguard.staffguard.user.UserRepository;
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
        long count = userRepository.count();
        int base = 36000 + (int)(count / 3) * 1000;
        int offset = random.nextInt(900) + 50;
        int id = base + offset;
        if (id > 99999) id = 90000 + random.nextInt(9999);
        return id;
    }
}