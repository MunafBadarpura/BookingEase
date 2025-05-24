package com.munaf.airBnbApp.repositories;

import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.Inventory;
import com.munaf.airBnbApp.entities.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);


    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.city = :city
                  AND i.closed = false
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
            GROUP BY i.hotel, i.room
            HAVING COUNT(i.date) = :dateCount
            """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("numberOfRooms") Integer numberOfRooms,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );


    @Query("""
            SELECT DISTINCT i.hotel
            FROM Inventory i
            WHERE i.closed = false
                  AND i.date > :today
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= 1
            """)
    Page<Hotel> findAllHotelsWithAvailableInventory(LocalDate today, Pageable pageable);



    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND i.closed = false
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") Integer numberOfRooms
    );
}
