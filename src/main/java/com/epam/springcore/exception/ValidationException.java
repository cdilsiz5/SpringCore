package com.epam.springcore.exception;



import java.time.LocalDateTime;
import java.util.Map;



public class ValidationException {

    private Map<String, String> validationErrors;
    private int statusCode;
    private String exceptionType;
    private LocalDateTime errorTime;

    public ValidationException(LocalDateTime errorTime, String exceptionType, int statusCode, Map<String, String> validationErrors) {
        this.errorTime = errorTime;
        this.exceptionType = exceptionType;
        this.statusCode = statusCode;
        this.validationErrors = validationErrors;
    }

    public ValidationException() {
    }

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}