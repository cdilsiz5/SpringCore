package com.epam.gymcrm.initializer;

import com.epam.gymcrm.model.TrainingType;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.model.enums.Specialization;
import com.epam.gymcrm.repository.TrainingTypeRepository;
import com.epam.gymcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TrainingTypeInitializer {

    private final TrainingTypeRepository trainingTypeRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        initTrainingTypes();
        initSystemAdminUser();
    }

    private void initTrainingTypes() {
        for (Specialization specialization : Specialization.values()) {
            if (!trainingTypeRepository.existsByName(specialization)) {
                TrainingType type = TrainingType.builder()
                        .name(specialization)
                        .build();
                trainingTypeRepository.save(type);
            }
        }
    }

    private void initSystemAdminUser() {
        final String ADMIN_USERNAME = "system-admin";

        if (!userRepository.existsByUsername(ADMIN_USERNAME)) {
            User admin = User.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .username(ADMIN_USERNAME)
                    .password("admin123")
                    .userActive(false)
                    .build();
            userRepository.save(admin);
        }
    }
}
