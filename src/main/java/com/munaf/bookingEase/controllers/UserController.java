package com.munaf.bookingEase.controllers;

import com.munaf.bookingEase.dtos.GuestDto;
import com.munaf.bookingEase.dtos.UpdateUserRequestDto;
import com.munaf.bookingEase.dtos.UserDto;
import com.munaf.bookingEase.services.GuestService;
import com.munaf.bookingEase.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final GuestService guestService;

    public UserController(UserService userService, GuestService guestService) {
        this.userService = userService;
        this.guestService = guestService;
    }


    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() {
        return new ResponseEntity<>(userService.getUserProfile(), HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateUserProfile(@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto) {
        return new ResponseEntity<>(userService.updateUserProfile(updateUserRequestDto), HttpStatus.OK);
    }


    @GetMapping("/guests")
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return new ResponseEntity<>(guestService.getAllGuests(), HttpStatus.OK);
    }

    @PostMapping("/guests")
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody @Valid GuestDto guestDto) {
        return new ResponseEntity<>(guestService.addNewGuest(guestDto), HttpStatus.CREATED);
    }

    @PutMapping("guests/{guestId}")
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Long guestId, @RequestBody @Valid GuestDto guestDto) {
        return new ResponseEntity<>(guestService.updateGuest(guestId, guestDto), HttpStatus.OK);
    }

    @DeleteMapping("guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }

}
