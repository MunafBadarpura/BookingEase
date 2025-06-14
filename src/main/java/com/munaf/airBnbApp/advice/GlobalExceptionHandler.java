package com.munaf.airBnbApp.advice;

import com.munaf.airBnbApp.exceptions.BookingExpiredException;
import com.munaf.airBnbApp.exceptions.InvalidInputException;
import com.munaf.airBnbApp.exceptions.ResourceNotFoundException;
import com.munaf.airBnbApp.exceptions.UnAuthorisedException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError, HttpStatus status) {
        return new ResponseEntity<>(new ApiResponse(apiError, status), status);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationError(MethodArgumentNotValidException exception){
        List<String> errors = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .message("Input Validation Error")
                .subErrors(errors)
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    // for list dto
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = exception
                .getConstraintViolations()
                .stream()
                .map(error -> error.getMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .message("Input Validation Error")
                .subErrors(errors)
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(UnAuthorisedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnAuthorisedException(UnAuthorisedException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingExpiredException.class)
    public ResponseEntity<ApiResponse<?>> handleBookingExpiredException(BookingExpiredException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();

        return buildErrorResponseEntity(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleInsufficientAuthenticationException(Exception e) {
        ApiError apiError = ApiError.builder()
                .message("Unauthorized access: " + e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError, HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handlerException(Exception e) {
        ApiError apiError = ApiError.builder()
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
