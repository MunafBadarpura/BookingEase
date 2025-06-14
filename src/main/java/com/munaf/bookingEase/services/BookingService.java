package com.munaf.bookingEase.services;

import com.munaf.bookingEase.dtos.BookingDto;
import com.munaf.bookingEase.dtos.BookingRequest;
import com.munaf.bookingEase.dtos.GuestDto;
import com.munaf.bookingEase.entities.enums.BookingStatus;
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
