package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.RoomDto;
import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.Room;
import com.munaf.airBnbApp.exceptions.ResourceNotFoundException;
import com.munaf.airBnbApp.repositories.HotelRepository;
import com.munaf.airBnbApp.repositories.RoomRepository;
import com.munaf.airBnbApp.services.InventoryService;
import com.munaf.airBnbApp.services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RoomServiceIMPL implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    public RoomServiceIMPL(RoomRepository roomRepository, HotelRepository hotelRepository, InventoryService inventoryService, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.inventoryService = inventoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public RoomDto createNewRoomInHotel(Long hotelId, RoomDto roomDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        // create inventory as soon as room is created and hotel is active
        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        List<Room> rooms = hotel.getRooms();
        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .toList();
    }

    @Override
    public RoomDto getRoomByHotelIdAndRoomId(Long hotelId, Long roomId) {
//        Room room = roomRepository.findById(roomId)
//                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId));
//        return modelMapper.map(room, RoomDto.class);

        Room room = roomRepository.findByIdAndHotel_Id(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId + " For Hotel Id : " + hotelId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public Boolean deleteRoomByHotelIdAndRoomId(Long hotelId, Long roomId) {
        Room room = roomRepository.findByIdAndHotel_Id(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId + " For Hotel Id : " + hotelId));

        // Delete inventories for this room
        inventoryService.deleteAllInventoriesForRoom(room);

        roomRepository.deleteById(roomId);
        return true;
    }
}
