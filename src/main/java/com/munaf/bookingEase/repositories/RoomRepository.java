package com.munaf.bookingEase.repositories;

import com.munaf.bookingEase.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByIdAndHotel_Id(Long roomId, Long hotelId);

    boolean existsByIdAndHotel_Id(Long roomId, Long hotelId);
}
