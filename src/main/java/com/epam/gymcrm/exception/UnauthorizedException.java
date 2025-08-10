package com.epam.gymcrm.exception;

import org.springframework.http.HttpStatus;


public class UnauthorizedException extends ApiException {
    private static final long serialVersionUID = 1L;
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}