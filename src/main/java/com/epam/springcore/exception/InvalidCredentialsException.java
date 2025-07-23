package com.epam.springcore.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

