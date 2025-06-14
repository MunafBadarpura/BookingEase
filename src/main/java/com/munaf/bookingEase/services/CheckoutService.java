package com.munaf.bookingEase.services;

import com.munaf.bookingEase.entities.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}
