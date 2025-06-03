package com.munaf.airBnbApp.dtos.authDtos;

import lombok.Data;

@Data
public class SignupRequestDto {

    private String name;
    private String email;
    private String password;

}
