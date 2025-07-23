package com.epam.springcore.initializer;

import com.epam.springcore.model.TrainingType;
import com.epam.springcore.model.enums.Specialization;
import com.epam.springcore.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TrainingTypeInitializer {

    private final TrainingTypeRepository trainingTypeRepository;

    @PostConstruct
    public void initTrainingTypes() {
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
