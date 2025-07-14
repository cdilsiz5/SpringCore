package com.epam.springcore.exception;

import org.springframework.http.HttpStatus;

    public class GymNotFoundException extends ApiException {
    public GymNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
