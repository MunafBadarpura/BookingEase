package com.munaf.bookingEase.repositories;

import com.munaf.bookingEase.entities.Booking;
import com.munaf.bookingEase.entities.Hotel;
import com.munaf.bookingEase.entities.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Booking> findByHotelAndBookingStatus(Hotel hotel, BookingStatus bookingStatus);
}
