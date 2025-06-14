package com.munaf.bookingEase.configs;


import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripConfig {

    public StripConfig(@Value("${stripe.secret.key}") String stripSecretKey) {
        Stripe.apiKey = stripSecretKey;
    }

}
