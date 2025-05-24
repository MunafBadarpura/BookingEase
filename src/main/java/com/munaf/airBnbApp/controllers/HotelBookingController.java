package com.munaf.airBnbApp.controllers;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.BookingRequest;
import com.munaf.airBnbApp.dtos.GuestDto;
import com.munaf.airBnbApp.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    public HotelBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingRequest bookingRequest) {
        return new ResponseEntity<>(bookingService.initializeBooking(bookingRequest), HttpStatus.OK);
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuestsToBooking(@PathVariable Long bookingId, @RequestBody List<GuestDto> guestDtoList) {
        return new ResponseEntity<>(bookingService.addGuestsToBooking(bookingId, guestDtoList), HttpStatus.OK);
    }

}
