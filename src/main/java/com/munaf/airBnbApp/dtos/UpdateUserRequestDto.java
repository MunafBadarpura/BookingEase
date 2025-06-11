package com.munaf.airBnbApp.dtos;

import com.munaf.airBnbApp.entities.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequestDto {
    private String name;

    private LocalDate dateOfBirth;

    private Gender gender;

}
