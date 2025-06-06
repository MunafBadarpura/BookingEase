package com.munaf.airBnbApp.controllers;

import com.munaf.airBnbApp.services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    public StripeWebhookController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }
}
