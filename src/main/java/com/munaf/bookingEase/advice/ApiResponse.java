package com.munaf.bookingEase.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    @JsonFormat(pattern = "hh:mm:ss a dd-MM-yyyy") // a = AM or PM
    private LocalDateTime timestamp = LocalDateTime.now();
    private HttpStatus status;
    private T data;
    private ApiError apiError;

    public ApiResponse(T data, HttpStatus status) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse(ApiError apiError, HttpStatus status) {
        this.status = status;
        this.apiError = apiError;
    }
}
