package com.epam.springcore.initializer;

import com.epam.springcore.model.TrainingType;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypeDataLoader implements CommandLineRunner {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public void run(String... args) {
        for (Specialization specialization : Specialization.values()) {
            if (!trainingTypeRepository.existsByName(specialization)) {
                TrainingType type = TrainingType.builder()
                        .name(specialization)
                        .build();
                trainingTypeRepository.save(type);
            }
        }
    }
}
