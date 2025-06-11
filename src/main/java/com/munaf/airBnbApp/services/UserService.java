package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.UpdateUserRequestDto;
import com.munaf.airBnbApp.dtos.UserDto;

public interface UserService {
    UserDto getUserProfile();

    UserDto updateUserProfile(UpdateUserRequestDto updateUserRequestDto);
}
