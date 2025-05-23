package com.munaf.airBnbApp.repositories;

import com.munaf.airBnbApp.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingRepository extends JpaRepository<Booking, Long> {
}
