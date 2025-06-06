package com.munaf.airBnbApp.controllers;

import com.munaf.airBnbApp.dtos.*;
import com.munaf.airBnbApp.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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


    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<BookingPaymentInitResponseDto> initiateBookingPayment(@PathVariable Long bookingId) {
        BookingPaymentInitResponseDto bookingPaymentInitResponseDto = new BookingPaymentInitResponseDto(bookingService.initiateBookingPayment(bookingId));
        return new ResponseEntity<>(bookingPaymentInitResponseDto, HttpStatus.OK);
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}/status")
    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(@PathVariable Long bookingId) {
        return ResponseEntity.ok(new BookingStatusResponseDto(bookingService.getBookingStatus(bookingId)));
    }

}
