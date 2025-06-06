package com.munaf.airBnbApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.munaf.airBnbApp.entities.enums.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;

    @JsonProperty("hotel")
    private HotelDto hotelDto;

    @JsonProperty("room")
    private RoomDto roomDto;

    @JsonProperty("user")
    private UserDto userDto;

    private Integer numberOfRooms; // roomCount

    private BookingStatus bookingStatus;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonProperty("guest")
    private Set<GuestDto> guestDtos;

    private BigDecimal amount;

}
