package com.munaf.bookingEase.configs;


import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripConfig {

    @Value("${stripe.secret.key}")
    private String stripSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripSecretKey; // setting up key in stripe
    }


//    public StripConfig(@Value("${stripe.secret.key}") String stripSecretKey) {
//        Stripe.apiKey = stripSecretKey;
//    }

}
