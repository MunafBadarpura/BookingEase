package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelSearchRequest;
import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.Inventory;
import com.munaf.airBnbApp.entities.Room;
import com.munaf.airBnbApp.repositories.InventoryRepository;
import com.munaf.airBnbApp.services.InventoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class InventoryServiceIMPL implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    public InventoryServiceIMPL(InventoryRepository inventoryRepository, ModelMapper modelMapper) {
        this.inventoryRepository = inventoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        while (!today.isAfter(endDate)) { // today!= endDate
            Inventory inventory = Inventory.builder()
                    .room(room)
                    .hotel(room.getHotel())
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .bookedCount(0)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);

            today = today.plusDays(1);
        }

    }

    @Override
    public void deleteAllInventoriesForRoom(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDto> searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1; // 12-20 = 9

        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getNumberOfRooms(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                dateCount,
                pageable
        );

        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));

        // ChronoUnit
        // query
    }

    @Override
    public Page<HotelDto> getAllHotels(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        LocalDate today = LocalDate.now();

        Page<Hotel> hotelPage = inventoryRepository.findAllHotelsWithAvailableInventory(today, pageable);
        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));
    }
}
