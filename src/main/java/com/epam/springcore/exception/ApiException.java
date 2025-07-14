package com.epam.springcore.exception;



import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class ApiException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;
    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
