package com.munaf.airBnbApp.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateInventoryRequestDto {

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Surge factor is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Surge factor must be greater than 0")
    private BigDecimal surgeFactor;

    private Boolean closed;



}
