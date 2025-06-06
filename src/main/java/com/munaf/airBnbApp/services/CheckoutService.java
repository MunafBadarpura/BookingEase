package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.entities.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}
