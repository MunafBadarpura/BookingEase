package com.munaf.airBnbApp.dtos.authDtos;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String email;
    private String password;

}
