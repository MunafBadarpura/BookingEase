package com.munaf.bookingEase.controllers;

import com.munaf.bookingEase.dtos.HotelInfoDto;
import com.munaf.bookingEase.dtos.HotelSearchRequest;
import com.munaf.bookingEase.services.HotelService;
import com.munaf.bookingEase.services.InventoryService;
import com.munaf.bookingEase.utils.PageModel;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
public class HotelBrowseController {


    private final InventoryService inventoryService;
    private final HotelService hotelService;

    public HotelBrowseController(InventoryService inventoryService, HotelService hotelService) {
        this.inventoryService = inventoryService;
        this.hotelService = hotelService;
    }

    @GetMapping("/searchAll")
    public ResponseEntity<PageModel> getAllHotels(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                  @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return new ResponseEntity<>(inventoryService.getAllHotels(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PageModel> searchHotel(@RequestBody @Valid HotelSearchRequest hotelSearchRequest,
                                                           @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                           @RequestParam(required = false, defaultValue = "10") Integer pageSize
                                      ) {
        return new ResponseEntity<>(inventoryService.searchHotel(hotelSearchRequest, pageNo, pageSize), HttpStatus.OK);
    }


    @GetMapping("/info/{hotelId}")
    public ResponseEntity<HotelInfoDto> getHotelInfoById(@PathVariable Long hotelId) {
        return new ResponseEntity<>(hotelService.getHotelInfoById(hotelId), HttpStatus.OK);
    }


//    @GetMapping("/search")
//    public ResponseEntity<Page<HotelDto>> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest,
//                                                      @RequestParam(required = false, defaultValue = "1") Integer pageNo,
//                                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize
//    ) {
//        return new ResponseEntity<>(inventoryService.searchHotel(hotelSearchRequest, pageNo, pageSize), HttpStatus.OK);
//    }

//    @GetMapping("/searchAll")
//    public ResponseEntity<Page<HotelDto>> getAllHotels(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
//                                                       @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
//        return new ResponseEntity<>(inventoryService.getAllHotels(pageNo, pageSize), HttpStatus.OK);
//    }
}
