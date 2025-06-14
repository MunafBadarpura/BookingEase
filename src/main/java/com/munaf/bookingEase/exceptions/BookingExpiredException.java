package com.munaf.bookingEase.exceptions;

public class BookingExpiredException extends RuntimeException {
    public BookingExpiredException(String msg) {
        super(msg);
    }
}
