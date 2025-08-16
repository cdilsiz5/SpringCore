package com.epam.gymcrm.exception;



import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiException extends RuntimeException  {
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;
    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
//
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
