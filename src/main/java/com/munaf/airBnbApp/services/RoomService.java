package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoomInHotel(Long hotelId, RoomDto roomDto);

    List<RoomDto> getAllRoomByHotelId(Long hotelId);

    RoomDto getRoomByHotelIdAndRoomId(Long hotelId, Long roomId);

    Boolean deleteRoomByHotelIdAndRoomId(Long hotelId, Long roomId);

    RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto);
}
