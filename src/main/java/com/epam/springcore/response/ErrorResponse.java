package com.epam.springcore.response;


import java.time.LocalDateTime;


public class ErrorResponse {
    private String message;
    private String exceptionType;
    private int statusCode;
    private LocalDateTime errorTime;

    public ErrorResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public LocalDateTime getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(LocalDateTime errorTime) {
        this.errorTime = errorTime;
    }

    public ErrorResponse(String message, String exceptionType, int statusCode, LocalDateTime errorTime) {
        this.message = message;
        this.exceptionType = exceptionType;
        this.statusCode = statusCode;
        this.errorTime = errorTime;

    }
}