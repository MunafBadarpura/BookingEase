package com.munaf.bookingEase.repositories;

import com.munaf.bookingEase.entities.Hotel;
import com.munaf.bookingEase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
}
