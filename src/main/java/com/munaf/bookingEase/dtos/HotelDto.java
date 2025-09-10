package com.munaf.bookingEase.dtos;

import com.munaf.bookingEase.entities.ContactInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class HotelDto {

    private Long id;

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    private String[] photos;

    private String[] amenities;

    private Boolean active;

    @Valid
    @NotNull(message = "Contact info is required")
    private ContactInfo contactInfo;

}
