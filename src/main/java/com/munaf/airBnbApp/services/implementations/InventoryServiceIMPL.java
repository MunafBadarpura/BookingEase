package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelPriceDto;
import com.munaf.airBnbApp.dtos.HotelSearchRequest;
import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.HotelMinPrice;
import com.munaf.airBnbApp.entities.Inventory;
import com.munaf.airBnbApp.entities.Room;
import com.munaf.airBnbApp.repositories.HotelMinPriceRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceIMPL implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final ModelMapper modelMapper;

    public InventoryServiceIMPL(InventoryRepository inventoryRepository, HotelMinPriceRepository hotelMinPriceRepository, ModelMapper modelMapper) {
        this.inventoryRepository = inventoryRepository;
        this.hotelMinPriceRepository = hotelMinPriceRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);
        List<Inventory> inventories = new ArrayList<>();

        while (!today.isAfter(endDate)) { // today!= endDate
            Inventory inventory = Inventory.builder()
                    .room(room)
                    .hotel(room.getHotel())
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .bookedCount(0)
                    .reservedCount(0)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .closed(false)
                    .build();
            inventories.add(inventory);
            today = today.plusDays(1);
        }
        inventoryRepository.saveAll(inventories);

    }

    @Override
    public void deleteAllInventoriesForRoom(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

//    @Override
//    public Page<HotelDto> searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
//        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1; // 12-20 = 9
//
//        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
//                hotelSearchRequest.getCity(),
//                hotelSearchRequest.getNumberOfRooms(),
//                hotelSearchRequest.getStartDate(),
//                hotelSearchRequest.getEndDate(),
//                dateCount,
//                pageable
//        );
//
//        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));
//    }


    @Override
    public Page<HotelPriceDto> searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1; // 12-20 = 9

        Page<HotelPriceDto> hotelPriceDtoPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                dateCount,
                pageable
        );

        return hotelPriceDtoPage;
    }

//    @Override
//    public Page<HotelDto> getAllHotels(Integer pageNo, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
//
//        Page<Hotel> hotelPage = inventoryRepository.findAllHotelsWithAvailableInventory(LocalDate.now(), pageable);
//        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));
//    }


    @Override
    public Page<HotelPriceDto> getAllHotels(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);

        Page<HotelPriceDto> hotelPriceDtoPage = hotelMinPriceRepository.findAllHotelsWithAvailableInventory(LocalDate.now(),  pageable);
        return hotelPriceDtoPage;
    }
}
