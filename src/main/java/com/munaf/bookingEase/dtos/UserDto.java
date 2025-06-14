package com.munaf.bookingEase.dtos;

import com.munaf.bookingEase.entities.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {

    private Long id;

    private String name;

    private String email;

    private LocalDate dateOfBirth;

    private Gender gender;

}
