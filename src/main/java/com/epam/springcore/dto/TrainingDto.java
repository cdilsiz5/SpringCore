package com.epam.springcore.dto;


public class TrainingDto {
    private String id;
    private String traineeId;
    private String trainerId;
    private String date;
    private String type;
    private int durationMinutes;

    public TrainingDto() {
    }

    public TrainingDto(String id, String traineeId, String trainerId, String date, String type, int durationMinutes) {
        this.id = id;
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.date = date;
        this.type = type;
        this.durationMinutes = durationMinutes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
