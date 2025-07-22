package com.epam.springcore.request.training;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTrainingRequest {


    @NotBlank(message = "Trainee ID cannot be blank")
    private String traineeId;

    @NotBlank(message = "Trainer ID cannot be blank")
    private String trainerId;

    @NotBlank(message = "Training date cannot be blank")
    private String date;

    @Min(value = 1, message = "Training type must be at least 1")
    @NotNull(message = "Training type cannot be null")
    private Long type;


    @Min(value = 1, message = "Training duration must be at least 1 minute")
    private Integer durationMinutes;

}
