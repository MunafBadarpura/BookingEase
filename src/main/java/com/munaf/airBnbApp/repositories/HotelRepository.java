package com.munaf.airBnbApp.repositories;

import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
}
