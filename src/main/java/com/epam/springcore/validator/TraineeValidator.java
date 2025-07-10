package com.epam.springcore.validator;

import com.epam.springcore.model.Trainee;

import java.time.LocalDate;

public class TraineeValidator {

    public static void validate(Trainee trainee) {
        if (trainee == null) throw new IllegalArgumentException("Trainee cannot be null");

        if (trainee.getDateOfBirth() == null || trainee.getDateOfBirth().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Date of birth must be in the past");

        if (trainee.getAddress() == null || trainee.getAddress().isBlank())
            throw new IllegalArgumentException("Address cannot be empty");

        if (trainee.getUserId() == null || trainee.getUserId().isBlank())
            throw new IllegalArgumentException("User ID cannot be empty");
    }
}
