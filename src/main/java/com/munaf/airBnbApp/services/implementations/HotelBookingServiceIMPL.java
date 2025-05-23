package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.BookingRequest;
import com.munaf.airBnbApp.repositories.HotelBookingRepository;
import com.munaf.airBnbApp.services.HotelBookingService;
import org.springframework.stereotype.Service;

@Service
public class HotelBookingServiceIMPL implements HotelBookingService {

    private final HotelBookingRepository hotelBookingRepository;

    public HotelBookingServiceIMPL(HotelBookingRepository hotelBookingRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
    }

    @Override
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        return null;
    }


}
