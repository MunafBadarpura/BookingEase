package com.munaf.bookingEase.services.implementations;

import com.munaf.bookingEase.dtos.UpdateUserRequestDto;
import com.munaf.bookingEase.dtos.UserDto;
import com.munaf.bookingEase.entities.User;
import com.munaf.bookingEase.repositories.UserRepository;
import com.munaf.bookingEase.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.munaf.bookingEase.utils.UserUtils.getCurrentUser;

@Service
public class UserServiceIMPL implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceIMPL(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User Not Found With Email : " + username));
    }


    @Override
    public UserDto getUserProfile() {
        return modelMapper.map(getCurrentUser(), UserDto.class);
    }

    @Override
    public UserDto updateUserProfile(UpdateUserRequestDto updateUserRequestDto) {
        User user = getCurrentUser();

        if (updateUserRequestDto.getName() != null) user.setName(updateUserRequestDto.getName());
        if (updateUserRequestDto.getGender() != null) user.setGender(updateUserRequestDto.getGender());
        if (updateUserRequestDto.getDateOfBirth() != null) user.setDateOfBirth(updateUserRequestDto.getDateOfBirth());

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }
}
