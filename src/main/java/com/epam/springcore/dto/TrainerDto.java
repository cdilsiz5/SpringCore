package com.epam.springcore.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDto {
    private Long id;
    private String specialization;
    private UserDto user;
    private List<TrainingDto> trainings;

}
