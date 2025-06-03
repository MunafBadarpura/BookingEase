package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.authDtos.LoginRequestDto;
import com.munaf.airBnbApp.dtos.authDtos.LoginResponseDto;
import com.munaf.airBnbApp.dtos.authDtos.SignupRequestDto;
import com.munaf.airBnbApp.dtos.authDtos.SignupResponseDto;

public interface AuthService {
    SignupResponseDto signup(SignupRequestDto signupRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    LoginResponseDto refresh(String refreshToken);
}
