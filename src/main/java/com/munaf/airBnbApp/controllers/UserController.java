package com.munaf.airBnbApp.controllers;

import com.munaf.airBnbApp.dtos.GuestDto;
import com.munaf.airBnbApp.dtos.UpdateUserRequestDto;
import com.munaf.airBnbApp.dtos.UserDto;
import com.munaf.airBnbApp.services.GuestService;
import com.munaf.airBnbApp.services.UserService;
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
    public ResponseEntity<UserDto> updateUserProfile(@RequestBody UpdateUserRequestDto updateUserRequestDto) {
        return new ResponseEntity<>(userService.updateUserProfile(updateUserRequestDto), HttpStatus.OK);
    }


    @GetMapping("/guests")
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return new ResponseEntity<>(guestService.getAllGuests(), HttpStatus.OK);
    }

    @PostMapping("/guests")
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody GuestDto guestDto) {
        return new ResponseEntity<>(guestService.addNewGuest(guestDto), HttpStatus.CREATED);
    }

    @PutMapping("guests/{guestId}")
    public ResponseEntity<GuestDto> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        return new ResponseEntity<>(guestService.updateGuest(guestId, guestDto), HttpStatus.OK);
    }

    @DeleteMapping("guests/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }

}
