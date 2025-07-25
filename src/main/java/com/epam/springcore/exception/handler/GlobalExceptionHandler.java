package com.epam.springcore.exception.handler;



import com.epam.springcore.exception.ApiException;
import com.epam.springcore.exception.ValidationException;
import com.epam.springcore.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice

public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ApiException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exception.getMessage());
        errorResponse.setExceptionType(exception.getClass().getSimpleName());
        errorResponse.setStatusCode(exception.getHttpStatus().value());
        errorResponse.setErrorTime(LocalDateTime.now());
        return ResponseEntity.status(exception.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationException> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ValidationException errorResponse = new ValidationException();
        errorResponse.setExceptionType(exception.getClass().getSimpleName());
        errorResponse.setValidationErrors(validationErrors);
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setErrorTime(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

}
