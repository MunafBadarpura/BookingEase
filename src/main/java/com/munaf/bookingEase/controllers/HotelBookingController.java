package com.munaf.bookingEase.controllers;

import com.munaf.bookingEase.dtos.*;
import com.munaf.bookingEase.services.BookingService;
import jakarta.validation.Valid;
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
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody @Valid BookingRequest bookingRequest) {
        return new ResponseEntity<>(bookingService.initializeBooking(bookingRequest), HttpStatus.OK);
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuestsToBooking(@PathVariable Long bookingId, @RequestBody @Valid List<GuestDto> guestDtoList) {
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
