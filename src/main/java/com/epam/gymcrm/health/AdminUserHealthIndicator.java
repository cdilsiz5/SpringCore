package com.epam.gymcrm.health;

import com.epam.gymcrm.repository.UserRepository;
import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

@Component
public class AdminUserHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;

    public AdminUserHealthIndicator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Health health() {
        boolean exists = userRepository.existsByUsername("system-admin");
        if (exists) {
            return Health.up().withDetail("system-admin", "Mevcut").build();
        } else {
            return Health.down().withDetail("system-admin", "Eksik").build();
        }
    }
}
