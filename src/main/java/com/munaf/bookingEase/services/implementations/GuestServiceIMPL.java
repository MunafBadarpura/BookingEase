package com.munaf.bookingEase.services.implementations;

import com.munaf.bookingEase.dtos.GuestDto;
import com.munaf.bookingEase.dtos.UserDto;
import com.munaf.bookingEase.entities.Guest;
import com.munaf.bookingEase.entities.User;
import com.munaf.bookingEase.exceptions.ResourceNotFoundException;
import com.munaf.bookingEase.exceptions.UnAuthorisedException;
import com.munaf.bookingEase.repositories.GuestRepository;
import com.munaf.bookingEase.services.GuestService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.munaf.bookingEase.utils.UserUtils.getCurrentUser;

@Service
public class GuestServiceIMPL implements GuestService {

    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    public GuestServiceIMPL(GuestRepository guestRepository, ModelMapper modelMapper) {
        this.guestRepository = guestRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GuestDto> getAllGuests() {
        User user = getCurrentUser();
        List<Guest> guests = guestRepository.findByUser(user);
        return guests.stream()
                .map(guest -> modelMapper.map(guest, GuestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public GuestDto addNewGuest(GuestDto guestDto) {
        User user = getCurrentUser();
        Guest guest = modelMapper.map(guestDto, Guest.class);
        guest.setUser(user);
        Guest savedGuest = guestRepository.save(guest);

        GuestDto newGuestDto = modelMapper.map(savedGuest, GuestDto.class);
        newGuestDto.setUserDto(modelMapper.map(savedGuest.getUser(), UserDto.class));
        return newGuestDto;
    }

    @Override
    public GuestDto updateGuest(Long guestId, GuestDto guestDto) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest Not Found With Id : " + guestId));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new UnAuthorisedException("Guest Does Not Belongs To This User With Id : " + user.getId());

        guest = modelMapper.map(guestDto, Guest.class);
        guest.setUser(user);
        guest.setId(guestId);

        Guest savedGuest = guestRepository.save(guest);

        GuestDto newGuestDto = modelMapper.map(savedGuest, GuestDto.class);
        newGuestDto.setUserDto(modelMapper.map(savedGuest.getUser(), UserDto.class));
        return newGuestDto;
    }

    @Override
    public void deleteGuest(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("Guest Not Found With Id : " + guestId));

        User user = getCurrentUser();
        if(!user.equals(guest.getUser())) throw new UnAuthorisedException("Guest Does Not Belongs To This User With Id : " + user.getId());

        guestRepository.deleteById(guestId);
    }
}
