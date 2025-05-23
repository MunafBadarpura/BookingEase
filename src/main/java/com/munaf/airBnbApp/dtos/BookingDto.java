package com.munaf.airBnbApp.dtos;

import com.munaf.airBnbApp.entities.enums.BookingStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;

    private HotelDto hotelDto;

    private RoomDto roomDto;

    private UserDto userDto;

    private Integer numberOfRooms; // roomCount

    private BookingStatus bookingStatus;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<GuestDto> guestDtos;

}
