package com.munaf.airBnbApp.dtos;

import com.munaf.airBnbApp.entities.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class GuestDto {

    @NotNull(message = "User is required")
    private UserDto userDto;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than or equal to 120")
    private Integer age;

}
