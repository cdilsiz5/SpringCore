package com.epam.springcore.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeDto {
    private Long id;
    private String address;
    private LocalDate dateOfBirth;
    private UserDto user;
    private List<TrainerDto> trainers;
    private List<TrainingDto> trainings;
}
