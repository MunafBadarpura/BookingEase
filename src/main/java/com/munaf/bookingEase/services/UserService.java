package com.munaf.bookingEase.services;

import com.munaf.bookingEase.dtos.UpdateUserRequestDto;
import com.munaf.bookingEase.dtos.UserDto;

public interface UserService {
    UserDto getUserProfile();

    UserDto updateUserProfile(UpdateUserRequestDto updateUserRequestDto);
}
