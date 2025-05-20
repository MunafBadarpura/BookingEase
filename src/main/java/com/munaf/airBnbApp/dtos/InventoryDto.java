package com.munaf.airBnbApp.dtos;

import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.Room;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InventoryDto {

    private Long id;

    private Hotel hotel;

    private Room room;

    private LocalDate date;

    private Integer bookedCount = 0;

    private Integer totalCount;

    private BigDecimal surgeFactor;

    private BigDecimal price; // basePrice * surgeFactor

    private String city;

    private Boolean closed;

}
