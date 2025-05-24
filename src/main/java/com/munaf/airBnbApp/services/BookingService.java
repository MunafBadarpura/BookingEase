package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.BookingRequest;
import com.munaf.airBnbApp.dtos.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuestsToBooking(Long bookingId, List<GuestDto> guestDtoList);
}
