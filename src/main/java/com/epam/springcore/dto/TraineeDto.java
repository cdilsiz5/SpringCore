package com.epam.springcore.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeDto {
    private String address;
    private LocalDate dateOfBirth;
    private UserDto user;
    private List<TrainerDto> trainers;
}
