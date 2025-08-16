package com.epam.gymcrm.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    private static final long serialVersionUID = 1L;
    public UserNotFoundException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}