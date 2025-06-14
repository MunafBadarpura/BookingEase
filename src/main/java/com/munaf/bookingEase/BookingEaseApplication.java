package com.munaf.bookingEase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookingEaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingEaseApplication.class, args);
	}

}
