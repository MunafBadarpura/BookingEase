package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelInfoDto;
import com.munaf.airBnbApp.dtos.HotelReportDto;
import com.munaf.airBnbApp.entities.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long hotelId);

    HotelDto updateHotelById(Long hotelId,HotelDto updateHotelDto);

    Boolean deleteHotelById(Long hotelId);

    HotelDto activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);

    List<HotelDto> getAllHotels();

    List<BookingDto> getAllBookings(Long hotelId, BookingStatus bookingStatus);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);
}
