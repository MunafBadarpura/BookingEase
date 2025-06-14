package com.munaf.bookingEase.services.implementations;

import com.munaf.bookingEase.dtos.HotelPriceDto;
import com.munaf.bookingEase.dtos.HotelSearchRequest;
import com.munaf.bookingEase.dtos.InventoryDto;
import com.munaf.bookingEase.dtos.UpdateInventoryRequestDto;
import com.munaf.bookingEase.entities.*;
import com.munaf.bookingEase.exceptions.ResourceNotFoundException;
import com.munaf.bookingEase.exceptions.UnAuthorisedException;
import com.munaf.bookingEase.repositories.HotelMinPriceRepository;
import com.munaf.bookingEase.repositories.InventoryRepository;
import com.munaf.bookingEase.repositories.RoomRepository;
import com.munaf.bookingEase.services.InventoryService;
import com.munaf.bookingEase.utils.PageModel;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.munaf.bookingEase.utils.UserUtils.getCurrentUser;

@Service
public class InventoryServiceIMPL implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    public InventoryServiceIMPL(InventoryRepository inventoryRepository, HotelMinPriceRepository hotelMinPriceRepository, RoomRepository roomRepository, ModelMapper modelMapper) {
        this.inventoryRepository = inventoryRepository;
        this.hotelMinPriceRepository = hotelMinPriceRepository;
        this.roomRepository = roomRepository;
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
    public PageModel searchHotel(HotelSearchRequest hotelSearchRequest, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
        Long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1; // 12-20 = 9

        Page<HotelPriceDto> hotelPriceDtoPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                dateCount,
                pageable
        );

        return PageModel.builder()
                .content(hotelPriceDtoPage.getContent())
                .currentPageNumber(pageNo)
                .currentPageSize(pageSize)
                .totalPageNumber(hotelPriceDtoPage.getTotalPages())
                .totalRecords(hotelPriceDtoPage.getTotalElements())
                .build();
    }

//    @Override
//    public Page<HotelDto> getAllHotels(Integer pageNo, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNo-1, pageSize);
//
//        Page<Hotel> hotelPage = inventoryRepository.findAllHotelsWithAvailableInventory(LocalDate.now(), pageable);
//        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));
//    }


    @Override
    public PageModel getAllHotels(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo-1, pageSize);

        Page<HotelPriceDto> hotelPriceDtoPage = hotelMinPriceRepository.findAllHotelsWithAvailableInventory(LocalDate.now(),  pageable);

        return PageModel.builder()
                .content(hotelPriceDtoPage.getContent())
                .currentPageNumber(pageNo)
                .currentPageSize(pageSize)
                .totalPageNumber(hotelPriceDtoPage.getTotalPages())
                .totalRecords(hotelPriceDtoPage.getTotalElements())
                .build();
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId));

        // CHECK THAT THIS HOTEL BELONGS TO CURRENT USER
        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) throw new UnAuthorisedException("Room Does Not Belongs To This User With Id : " + user.getId());

        List<Inventory> inventories = inventoryRepository.findByRoomOrderByDate(room);

        return inventories.stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDto.class))
                .toList();
    }

    @Override
    @Transactional
    public void updateInventories(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId));

        // CHECK THAT THIS HOTEL BELONGS TO CURRENT USER
        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) throw new UnAuthorisedException("Room Does Not Belongs To This User With Id : " + user.getId());

        List<Inventory> lockInventoryBeforeUpdate = inventoryRepository.getAndLockInventoryBeforeUpdate(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate()
        );

        inventoryRepository.updateInventory(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getSurgeFactor(),
                updateInventoryRequestDto.getClosed()
        );

    }
}
























