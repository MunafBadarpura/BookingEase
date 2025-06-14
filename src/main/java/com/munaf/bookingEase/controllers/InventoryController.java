package com.munaf.bookingEase.controllers;

import com.munaf.bookingEase.dtos.InventoryDto;
import com.munaf.bookingEase.dtos.UpdateInventoryRequestDto;
import com.munaf.bookingEase.services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId) {
        return new ResponseEntity<>(inventoryService.getAllInventoryByRoom(roomId), HttpStatus.OK);
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventories(@PathVariable Long roomId, @RequestBody @Valid UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventories(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }

}
