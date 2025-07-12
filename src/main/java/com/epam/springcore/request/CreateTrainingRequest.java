package com.epam.springcore.request;
import jakarta.validation.constraints.*;

public class CreateTrainingRequest {

    @NotBlank(message = "Trainee ID cannot be blank")
    private String traineeId;

    @NotBlank(message = "Trainer ID cannot be blank")
    private String trainerId;

    @NotBlank(message = "Training date cannot be blank")
    private String date;

    @NotBlank(message = "Training type cannot be blank")
    private String type;

    @Min(value = 1, message = "Training duration must be at least 1 minute")
    private int durationMinutes;

    public CreateTrainingRequest(String traineeId, String trainerId, String date, String type, int durationMinutes) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.date = date;
        this.type = type;
        this.durationMinutes = durationMinutes;
    }

    public CreateTrainingRequest() {
    }

    public String getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(String traineeId) {
        this.traineeId = traineeId;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
