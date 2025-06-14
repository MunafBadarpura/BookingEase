package com.munaf.bookingEase.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the future or in the present")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

}
