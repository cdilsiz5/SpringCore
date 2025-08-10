package com.epam.gymcrm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingResponse {

    private Long id;

    private Long traineeId;
    private String traineeFirstName;
    private String traineeLastName;

    private Long trainerId;
    private String trainerFirstName;
    private String trainerLastName;

    private Long trainingTypeId;
    private String trainingTypeName;

    private LocalDate date;
    private Integer durationMinutes;
}
