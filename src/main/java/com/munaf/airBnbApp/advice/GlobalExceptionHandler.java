package com.munaf.airBnbApp.advice;

import com.munaf.airBnbApp.exceptions.InvalidInputException;
import com.munaf.airBnbApp.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse(apiError, status), status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidInputException(InvalidInputException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handlerException(Exception e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
