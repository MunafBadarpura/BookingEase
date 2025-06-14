package com.munaf.bookingEase.services;

import com.munaf.bookingEase.dtos.BookingDto;
import com.munaf.bookingEase.dtos.HotelDto;
import com.munaf.bookingEase.dtos.HotelInfoDto;
import com.munaf.bookingEase.dtos.HotelReportDto;
import com.munaf.bookingEase.entities.enums.BookingStatus;

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
