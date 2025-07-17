package com.epam.springcore.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class InvalidCredentialsException extends ApiException implements Serializable {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

