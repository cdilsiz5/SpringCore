package com.epam.springcore.model;

import com.epam.springcore.model.enums.TrainingType;

public class Training {
    private String id;
    private String traineeId;
    private String trainerId;
    private String date;
    private TrainingType type;
    private int durationMinutes;
    private static int counter = 0;

    public Training(String traineeId, String trainerId, String date, TrainingType type, int durationMinutes) {
        this.traineeId = traineeId;
        this.trainerId = trainerId;
        this.date = date;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.id = String.valueOf(++counter);

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

    public TrainingType getType() {
        return type;
    }

    public void setType(TrainingType type) {
        this.type = type;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Training.counter = counter;
    }

    @Override
    public String toString() {
        return "Training{" +
                "id='" + id + '\'' +
                ", traineeId='" + traineeId + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", durationMinutes=" + durationMinutes +
                '}';
    }
}