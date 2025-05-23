package com.munaf.airBnbApp.dtos;

import com.munaf.airBnbApp.entities.enums.Gender;
import lombok.Data;


@Data
public class GuestDto {

    private Long id;

    private UserDto userDto;

    private String name;

    private Gender gender;

    private Integer age;

}
