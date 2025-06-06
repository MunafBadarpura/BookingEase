package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.BookingRequest;
import com.munaf.airBnbApp.dtos.GuestDto;
import com.munaf.airBnbApp.entities.enums.BookingStatus;
import com.stripe.model.Event;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest bookingRequest);

    BookingDto addGuestsToBooking(Long bookingId, List<GuestDto> guestDtoList);

    String initiateBookingPayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);
}
