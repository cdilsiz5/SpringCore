package com.epam.springcore.validator;

import com.epam.springcore.model.Training;

public class TrainingValidator {

    public static void validate(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training Object can not be null.");
        }

        if (isNullOrEmpty(training.getTraineeId())) {
            throw new IllegalArgumentException("Trainee ID can not be null.");
        }

        if (isNullOrEmpty(training.getTrainerId())) {
            throw new IllegalArgumentException("Trainer ID can not be null.");
        }

        if (isNullOrEmpty(training.getDate())) {
            throw new IllegalArgumentException("Date can not be null.");
        }

        if (isNullOrEmpty(training.getType())) {
            throw new IllegalArgumentException("Training Type can not be null.");
        }

        if (training.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Time Must be greater than zero.");
        }

    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
