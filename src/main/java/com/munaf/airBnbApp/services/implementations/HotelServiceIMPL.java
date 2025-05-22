package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelInfoDto;
import com.munaf.airBnbApp.dtos.RoomDto;
import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.exceptions.InvalidInputException;
import com.munaf.airBnbApp.exceptions.ResourceNotFoundException;
import com.munaf.airBnbApp.repositories.HotelRepository;
import com.munaf.airBnbApp.services.HotelService;
import com.munaf.airBnbApp.services.InventoryService;
import com.munaf.airBnbApp.services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class HotelServiceIMPL implements HotelService {

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomService roomService;
    private final ModelMapper modelMapper;

    public HotelServiceIMPL(HotelRepository hotelRepository, InventoryService inventoryService, RoomService roomService, ModelMapper modelMapper) {
        this.hotelRepository = hotelRepository;
        this.inventoryService = inventoryService;
        this.roomService = roomService;
        this.modelMapper = modelMapper;
    }

    @Override //admin
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name : {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        log.info("Created a new hotel with Id : {}", hotelDto.getId());
        return modelMapper.map(hotelRepository.save(hotel), HotelDto.class);
    }

    @Override // admin
    public HotelDto getHotelById(Long hotelId) {
        log.info("Getting a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override // admin
    public HotelDto updateHotelById(Long hotelId, HotelDto updateHotelDto) {
        log.info("Updating a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        Boolean active = hotel.getActive();
        modelMapper.map(updateHotelDto, hotel);
        hotel.setId(hotelId);
        hotel.setActive(active);
        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional // admin
    public Boolean deleteHotelById(Long hotelId) {
        log.info("Deleting a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));

        // Delete all future inventories
        //hotel.getRooms().forEach(room -> inventoryService.deleteFutureInventories(room));

        // Delete All rooms for this hotel
        hotel.getRooms().forEach(room -> roomService.deleteRoomByHotelIdAndRoomId(hotelId, room.getId()));

        hotelRepository.deleteById(hotelId);
        return true;
    }

    @Override
    @Transactional // admin
    public HotelDto activateHotel(Long hotelId) {
        log.info("Activating a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        if (hotel.getActive()) throw new InvalidInputException("Hotel Already Activated With Id : " + hotelId);
        hotel.setActive(true);
        hotel = hotelRepository.save(hotel);

        // Initialize inventory for all room in this hotel for a year
        hotel.getRooms().forEach(room -> inventoryService.initializeRoomForAYear(room));


        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override // guest/user
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));

        List<RoomDto> roomDtos = hotel.getRooms().stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .toList();

        return HotelInfoDto.builder()
                .hotelDto(modelMapper.map(hotel, HotelDto.class))
                .roomDtos(roomDtos)
                .build();
    }
}
