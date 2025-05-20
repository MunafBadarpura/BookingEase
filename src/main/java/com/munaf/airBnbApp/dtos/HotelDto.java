package com.munaf.airBnbApp.dtos;

import com.munaf.airBnbApp.entities.ContactInfo;
import lombok.Data;


@Data
public class HotelDto {

    private Long id;

    private String name;

    private String city;

    private String[] photos;

    private String[] amenities;

    private Boolean active;

    private ContactInfo contactInfo;

}
