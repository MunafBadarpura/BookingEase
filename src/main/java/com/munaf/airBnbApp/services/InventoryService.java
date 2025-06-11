package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.dtos.HotelSearchRequest;
import com.munaf.airBnbApp.dtos.InventoryDto;
import com.munaf.airBnbApp.dtos.UpdateInventoryRequestDto;
import com.munaf.airBnbApp.entities.Room;
import com.munaf.airBnbApp.utils.PageModel;

import java.util.List;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventoriesForRoom(Room room);

    PageModel searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize);

    PageModel getAllHotels(Integer pageNo, Integer pageSize);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventories(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
