package com.munaf.bookingEase.services;

import com.munaf.bookingEase.dtos.authDtos.LoginRequestDto;
import com.munaf.bookingEase.dtos.authDtos.LoginResponseDto;
import com.munaf.bookingEase.dtos.authDtos.SignupRequestDto;
import com.munaf.bookingEase.dtos.authDtos.SignupResponseDto;

public interface AuthService {
    SignupResponseDto signup(SignupRequestDto signupRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    LoginResponseDto refresh(String refreshToken);
}
