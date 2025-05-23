package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.BookingRequest;

public interface HotelBookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

}
