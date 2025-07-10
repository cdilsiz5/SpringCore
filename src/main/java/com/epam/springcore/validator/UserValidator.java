package com.epam.springcore.validator;

import com.epam.springcore.model.User;

public class UserValidator {

    public static void validate(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (user.getFirstName() == null || user.getFirstName().isBlank())
            throw new IllegalArgumentException("User first name cannot be empty");
        if (user.getLastName() == null || user.getLastName().isBlank())
            throw new IllegalArgumentException("User last name cannot be empty");
    }
}
