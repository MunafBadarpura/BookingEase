package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.GuestDto;

import java.util.List;

public interface GuestService {
    List<GuestDto> getAllGuests();

    GuestDto addNewGuest(GuestDto guestDto);

    GuestDto updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);
}
