package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.authDtos.LoginRequestDto;
import com.munaf.airBnbApp.dtos.authDtos.LoginResponseDto;
import com.munaf.airBnbApp.dtos.authDtos.SignupRequestDto;
import com.munaf.airBnbApp.dtos.authDtos.SignupResponseDto;
import com.munaf.airBnbApp.entities.User;
import com.munaf.airBnbApp.entities.enums.Role;
import com.munaf.airBnbApp.repositories.UserRepository;
import com.munaf.airBnbApp.services.AuthService;
import com.munaf.airBnbApp.utils.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceIMPL implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceIMPL(UserRepository userRepository, ModelMapper modelMapper, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }


    @Override
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        if (userRepository.existsByEmail(signupRequestDto.getEmail()))
            throw new AuthenticationServiceException("User Already Exists With Email " + signupRequestDto.getEmail());

        User newUser = modelMapper.map(signupRequestDto, User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(Set.of(Role.GUEST));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, SignupResponseDto.class);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    @Override
    public LoginResponseDto refresh(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User id not found with id : " + userId));

        String accessToken = jwtService.generateAccessToken(user);
        return new LoginResponseDto(userId, accessToken, refreshToken);
    }

}
