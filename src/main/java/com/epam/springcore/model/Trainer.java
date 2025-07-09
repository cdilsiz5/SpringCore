package com.epam.springcore.model;

import com.epam.springcore.model.enums.TrainingType;

public class Trainer {

    private TrainingType specialization;
    private String userId;

    public Trainer(TrainingType specialization, String userId) {
        this.specialization = specialization;
        this.userId = userId;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "specialization=" + specialization +
                ", userId='" + userId + '\'' +
                '}';
    }
}
