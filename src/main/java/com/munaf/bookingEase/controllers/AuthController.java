package com.munaf.bookingEase.controllers;

import com.munaf.bookingEase.dtos.authDtos.LoginRequestDto;
import com.munaf.bookingEase.dtos.authDtos.LoginResponseDto;
import com.munaf.bookingEase.dtos.authDtos.SignupRequestDto;
import com.munaf.bookingEase.dtos.authDtos.SignupResponseDto;
import com.munaf.bookingEase.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // signup -> signUp dto , return signUpResponseDtp
    // login, LoginDto, LoginRespinseDto
    // refresh -> JwtToeken

    @PostMapping("signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return new ResponseEntity<>(authService.signup(signupRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        httpServletResponse.addCookie(refreshTokenCookie);

        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }

    @PostMapping("refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElseThrow(() -> new AuthenticationServiceException("Refresh Token Is Invalid"));

        return new ResponseEntity<>(authService.refresh(refreshToken), HttpStatus.OK);
    }

}
