package com.epam.springcore.exception;


import java.time.LocalDateTime;
import java.util.Map;

public class ValidationException {
    private Map<String, String> validationErrors;
    private int statusCode;
    private String exceptionType;
    private LocalDateTime errorTime;

    public ValidationException(Map<String, String> validationErrors, int statusCode, String exceptionType, LocalDateTime errorTime) {
        this.validationErrors = validationErrors;
        this.statusCode = statusCode;
        this.exceptionType = exceptionType;
        this.errorTime = errorTime;
    }

    public ValidationException() {
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }
}
