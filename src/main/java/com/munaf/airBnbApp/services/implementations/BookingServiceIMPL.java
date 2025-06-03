package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.*;
import com.munaf.airBnbApp.entities.*;
import com.munaf.airBnbApp.entities.enums.BookingStatus;
import com.munaf.airBnbApp.exceptions.InvalidInputException;
import com.munaf.airBnbApp.exceptions.ResourceNotFoundException;
import com.munaf.airBnbApp.exceptions.UnAuthorisedException;
import com.munaf.airBnbApp.repositories.*;
import com.munaf.airBnbApp.services.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

import static com.munaf.airBnbApp.utils.UserUtils.getCurrentUser;

@Service
public class BookingServiceIMPL implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    public BookingServiceIMPL(BookingRepository bookingRepository, HotelRepository hotelRepository, RoomRepository roomRepository, InventoryRepository inventoryRepository, GuestRepository guestRepository, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.inventoryRepository = inventoryRepository;
        this.guestRepository = guestRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        User user = getCurrentUser();
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + bookingRequest.getHotelId()));
        // HotelDto hotelDto = modelMapper.map(hotel, HotelDto.class);

        Room room = roomRepository.findByIdAndHotel_Id(bookingRequest.getRoomId(), bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + bookingRequest.getRoomId() + " For Hotel Id : " + bookingRequest.getHotelId()));
        // RoomDto roomDto = modelMapper.map(room, RoomDto.class);

        List<Inventory> inventories = inventoryRepository.findAndLockAvailableInventory(bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getNumberOfRooms());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;
        if (inventories.size() != daysCount) {
            throw new InvalidInputException("Rooms Not Available For The Given Date Range");
        }

        // RESERVE THE ROOMS / UPDATE NUMBER OF ROOMS
        inventories.forEach(inventory -> {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getNumberOfRooms());
        });
        inventoryRepository.saveAll(inventories);

        // CREATE A BOOKING
        UserDto userDto = modelMapper.map(user, UserDto.class);

        Booking booking = Booking.builder()
                .hotel(hotel)
                .room(room)
                .user(user)
                .numberOfRooms(bookingRequest.getNumberOfRooms())
                .bookingStatus(BookingStatus.RESERVED)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .amount(BigDecimal.TEN) // TODO : CALCULATE AMOUNT
                .build();

        booking = bookingRepository.save(booking);
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        // bookingDto.setHotelDto(hotelDto);
        // bookingDto.setRoomDto(roomDto);
        bookingDto.setUserDto(userDto);

        return bookingDto;

    }

    @Override
    @Transactional
    public BookingDto addGuestsToBooking(Long bookingId, List<GuestDto> guestDtoList) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found With Id : " + bookingId));
        User user = getCurrentUser();

        if (hasBookingExpired(booking.getCreatedAt())) throw new IllegalStateException("Booking Has Been Already Expired");
        if (!user.equals(booking.getUser())) throw new UnAuthorisedException("Booking Does Not Belongs To This User With Id : " + user.getId());
        if (booking.getBookingStatus() != BookingStatus.RESERVED) throw new IllegalStateException("Booking Is Not Under Reserved State");

        // ADD GUESTS IN BOOKING
        UserDto userDto = modelMapper.map(user, UserDto.class);
        List<Guest> guestList = guestDtoList.stream()
                .map(guestDto -> {
                    guestDto.setUserDto(userDto);
                    return modelMapper.map(guestDto, Guest.class);
                })
                .toList();
        guestList = guestRepository.saveAll(guestList); // SAVE GUESTS IN GUEST TABLE

        booking.setGuests(new HashSet<>(guestList)); // SET GUESTS IN BOOKING
        booking.setBookingStatus(BookingStatus.GUEST_ADDED); // UPDATE BOOKING STATUS
        booking = bookingRepository.save(booking); // SAVE BOOKING AFTER SAVING GUESTS

        // CREATING A BOOKING DTO
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        List<GuestDto> guestDtos = guestList.stream()
                        .map(guest -> modelMapper.map(guest, GuestDto.class))
                        .toList();
        bookingDto.setUserDto(userDto);
        bookingDto.setGuestDtos(new HashSet<>(guestDtos));

        return bookingDto;
    }


    public Boolean hasBookingExpired(LocalDateTime createdAt) {
        return createdAt.plusMinutes(10).isBefore(LocalDateTime.now()); // valid for 10 minutes
    }




}
