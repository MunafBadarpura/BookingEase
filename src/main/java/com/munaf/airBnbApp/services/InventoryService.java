package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelPriceDto;
import com.munaf.airBnbApp.dtos.HotelSearchRequest;
import com.munaf.airBnbApp.entities.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventoriesForRoom(Room room);

    Page<HotelPriceDto> searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize);
    //Page<HotelDto> searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize);

    Page<HotelPriceDto> getAllHotels(Integer pageNo, Integer pageSize);
    //Page<HotelDto> getAllHotels(Integer pageNo, Integer pageSize);
}
