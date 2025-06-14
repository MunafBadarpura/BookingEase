package com.munaf.bookingEase.services;

import com.munaf.bookingEase.dtos.HotelSearchRequest;
import com.munaf.bookingEase.dtos.InventoryDto;
import com.munaf.bookingEase.dtos.UpdateInventoryRequestDto;
import com.munaf.bookingEase.entities.Room;
import com.munaf.bookingEase.utils.PageModel;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventoriesForRoom(Room room);

    PageModel searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize);

    PageModel getAllHotels(Integer pageNo, Integer pageSize);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventories(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
