package com.epam.springcore.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingDto {
    private Long id;
    private LocalDate date;
    private Integer duration;
    private TrainerDto trainer;
    private TraineeDto trainee;
    private String trainingType;
}
