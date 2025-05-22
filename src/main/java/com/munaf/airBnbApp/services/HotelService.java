package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelInfoDto;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long hotelId);

    HotelDto updateHotelById(Long hotelId,HotelDto updateHotelDto);

    Boolean deleteHotelById(Long hotelId);

    HotelDto activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
