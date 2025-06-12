package com.munaf.airBnbApp.exceptions;

public class BookingExpiredException extends RuntimeException {
    public BookingExpiredException(String msg) {
        super(msg);
    }
}
