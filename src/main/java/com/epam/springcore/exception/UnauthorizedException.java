package com.epam.springcore.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;


public class UnauthorizedException extends ApiException implements Serializable {
    private static final long serialVersionUID = 1L;
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}