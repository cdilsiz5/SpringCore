package com.epam.springcore.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDto {
    private LocalDate date;
    private int duration;
    private TrainerDto trainer;
    private TraineeDto trainee;
    private String trainingType;
}
