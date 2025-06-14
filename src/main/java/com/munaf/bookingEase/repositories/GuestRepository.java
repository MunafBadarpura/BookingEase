package com.munaf.bookingEase.repositories;

import com.munaf.bookingEase.entities.Guest;
import com.munaf.bookingEase.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}
