package com.epam.springcore.validator;

import com.epam.springcore.model.Trainer;

public class TrainerValidator {

    public static void validate(Trainer trainer) {
        if (trainer == null) throw new IllegalArgumentException("Trainer cannot be null");

        if (trainer.getSpecialization() == null)
            throw new IllegalArgumentException("Specialization must be provided");

        if (trainer.getUserId() == null || trainer.getUserId().isBlank())
            throw new IllegalArgumentException("User ID cannot be empty");
    }
}
