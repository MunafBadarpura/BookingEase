package com.munaf.airBnbApp.controllers;

import com.munaf.airBnbApp.dtos.BookingDto;
import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.dtos.HotelReportDto;
import com.munaf.airBnbApp.entities.enums.BookingStatus;
import com.munaf.airBnbApp.services.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("admin/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping()
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody @Valid HotelDto hotelDto) {
        return new ResponseEntity<>(hotelService.createNewHotel(hotelDto), HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        return new ResponseEntity<>(hotelService.getHotelById(hotelId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return new ResponseEntity<>(hotelService.getAllHotels(), HttpStatus.OK);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long hotelId, @RequestBody @Valid HotelDto updateHotelDto) {
        return new ResponseEntity<>(hotelService.updateHotelById(hotelId, updateHotelDto), HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId) {
        Boolean deleted = hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/activate/{hotelId}")
    public ResponseEntity<HotelDto> activateHotel(@PathVariable Long hotelId) {
        return new ResponseEntity<>(hotelService.activateHotel(hotelId), HttpStatus.OK);
    }


    @GetMapping("{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookings(@PathVariable Long hotelId, @RequestParam(required = false) BookingStatus bookingStatus) {
        System.out.println(bookingStatus);
        return new ResponseEntity<>(hotelService.getAllBookings(hotelId, bookingStatus), HttpStatus.OK);
    }

    @GetMapping("{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate
                                                         ) {
        if (startDate == null) startDate =  LocalDate.now();
        if (endDate == null)  endDate = LocalDate.now().plusMonths(1);
        return new ResponseEntity<>(hotelService.getHotelReport(hotelId, startDate, endDate), HttpStatus.OK);
    }
}
