package com.munaf.airBnbApp.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {

    private String city;
    private Integer numberOfRooms;
    private LocalDate startDate;
    private LocalDate endDate;

}
