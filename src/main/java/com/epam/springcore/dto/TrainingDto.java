package com.epam.springcore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingDto {
    private String id;
    private String traineeId;
    private String trainerId;
    private String date;
    private String type;
    private int durationMinutes;
}
