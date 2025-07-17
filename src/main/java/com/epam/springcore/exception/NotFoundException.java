package com.epam.springcore.exception;

import org.springframework.http.HttpStatus;
import java.io.Serializable;

public class NotFoundException extends ApiException implements Serializable {
    private static final long serialVersionUID = 1L;
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
