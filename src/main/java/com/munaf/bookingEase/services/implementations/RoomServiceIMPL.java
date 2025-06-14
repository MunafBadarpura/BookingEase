package com.munaf.bookingEase.services.implementations;

import com.munaf.bookingEase.dtos.RoomDto;
import com.munaf.bookingEase.entities.Hotel;
import com.munaf.bookingEase.entities.Inventory;
import com.munaf.bookingEase.entities.Room;
import com.munaf.bookingEase.entities.User;
import com.munaf.bookingEase.exceptions.InvalidInputException;
import com.munaf.bookingEase.exceptions.ResourceNotFoundException;
import com.munaf.bookingEase.exceptions.UnAuthorisedException;
import com.munaf.bookingEase.repositories.HotelRepository;
import com.munaf.bookingEase.repositories.InventoryRepository;
import com.munaf.bookingEase.repositories.RoomRepository;
import com.munaf.bookingEase.services.InventoryService;
import com.munaf.bookingEase.services.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.munaf.bookingEase.utils.UserUtils.getCurrentUser;

@Service
@Slf4j
public class RoomServiceIMPL implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;

    public RoomServiceIMPL(RoomRepository roomRepository, HotelRepository hotelRepository, InventoryService inventoryService, InventoryRepository inventoryRepository, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public RoomDto createNewRoomInHotel(Long hotelId, RoomDto roomDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));

        // CHECK THAT THIS HOTEL BELONGS TO CURRENT USER
        User user = getCurrentUser();
        if (!user.equals(hotel.getOwner())) throw new UnAuthorisedException("Hotel Does Not Belongs To This User With Id : " + user.getId());

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
        Room room = roomRepository.findByIdAndHotel_Id(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId + " For Hotel Id : " + hotelId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public Boolean deleteRoomByHotelIdAndRoomId(Long hotelId, Long roomId) {
        Room room = roomRepository.findByIdAndHotel_Id(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId + " For Hotel Id : " + hotelId));

        // CHECK THAT THIS HOTEL BELONGS TO CURRENT USER
        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) throw new UnAuthorisedException("Room Does Not Belongs To This User With Id : " + user.getId());

        // Delete inventories for this room
        inventoryService.deleteAllInventoriesForRoom(room);

        roomRepository.deleteById(roomId);
        return true;
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {
        Room room = roomRepository.findByIdAndHotel_Id(roomId, hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + roomId + " For Hotel Id : " + hotelId));

        // CHECK THAT THIS HOTEL BELONGS TO CURRENT USER
        User user = getCurrentUser();
        if (!user.equals(room.getHotel().getOwner())) throw new UnAuthorisedException("Room Does Not Belongs To This User With Id : " + user.getId());

        // Checking That roomCount is updated Or Not
        if (!Objects.equals(room.getTotalCount(), roomDto.getTotalCount())) {
            List<Inventory> inventories = inventoryRepository.findByRoomId(roomId);
            for (Inventory inventory : inventories) {
                if (inventory.getBookedCount() > roomDto.getTotalCount()) throw new InvalidInputException("Room Can Not Updated Because Inventory Booked Count Is Greater Then Room Total Count");
            }

            inventoryRepository.updateInventoryTotalCountByRoomId(roomId, roomDto.getTotalCount());
        }

        Room updatedRoom = modelMapper.map(roomDto, Room.class);
        updatedRoom.setId(room.getId());
        updatedRoom = roomRepository.save(updatedRoom);


        return modelMapper.map(updatedRoom, RoomDto.class);
    }
}
