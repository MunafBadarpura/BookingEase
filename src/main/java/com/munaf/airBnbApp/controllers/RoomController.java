package com.munaf.airBnbApp.controllers;

import com.munaf.airBnbApp.dtos.RoomDto;
import com.munaf.airBnbApp.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    ResponseEntity<RoomDto> createNewRoomInHotel(@PathVariable Long hotelId, @RequestBody RoomDto roomDto){
        return new ResponseEntity<>(roomService.createNewRoomInHotel(hotelId, roomDto), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<RoomDto>> getAllRoomByHotelId(@PathVariable Long hotelId) {
        return new ResponseEntity<>(roomService.getAllRoomByHotelId(hotelId), HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
    ResponseEntity<RoomDto> getRoomByHotelIdAndRoomId(@PathVariable Long hotelId, @PathVariable Long roomId) {
        return new ResponseEntity<>(roomService.getRoomByHotelIdAndRoomId(hotelId, roomId), HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    ResponseEntity<Void> deleteHotelIdAndRoomById(@PathVariable Long hotelId, @PathVariable Long roomId) {
        Boolean deleted = roomService.deleteHotelIdAndRoomById(hotelId, roomId);
        return ResponseEntity.noContent().build();
    }

}
