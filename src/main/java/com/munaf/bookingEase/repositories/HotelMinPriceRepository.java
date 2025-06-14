package com.munaf.bookingEase.repositories;

import com.munaf.bookingEase.dtos.HotelPriceDto;
import com.munaf.bookingEase.entities.Hotel;
import com.munaf.bookingEase.entities.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    @Query("""
            SELECT new com.munaf.bookingEase.dtos.HotelPriceDto(hmp.hotel, AVG(hmp.price))
            FROM HotelMinPrice hmp
            WHERE hmp.hotel.city = :city
                  AND hmp.hotel.active = true
                  AND hmp.date BETWEEN :startDate AND :endDate
            GROUP BY hmp.hotel
            """)
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );


    @Query("""
            SELECT new com.munaf.bookingEase.dtos.HotelPriceDto(hmp.hotel, AVG(hmp.price))
            FROM HotelMinPrice hmp
            WHERE hmp.hotel.active = true
                AND hmp.date > :today
            GROUP BY hmp.hotel
            """)
    Page<HotelPriceDto> findAllHotelsWithAvailableInventory(LocalDate today, Pageable pageable);



    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
