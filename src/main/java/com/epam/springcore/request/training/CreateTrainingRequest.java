package com.epam.springcore.request.training;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTrainingRequest {

    @Min(value = 1, message = "Trainee Id must be at 1")
    @NotNull(message = "Trainee Id duration cannot be null")
    private Long traineeId;

    @Min(value = 1, message = "Trainee Id must be at 1")
    @NotNull(message = "Trainee Id duration cannot be null")
    private Long trainerId;

    @NotBlank(message = "Training date cannot be blank")
    private String date;

    @Min(value = 1, message = "Training type must be at least 1")
    @NotNull(message = "Training type cannot be null")
    private Long type;

    @Min(value = 1, message = "Training duration must be at least 1 minute")
    @NotNull(message = "Training duration cannot be null")
    private Integer durationMinutes;


}
