package com.munaf.bookingEase.services.implementations;

import com.munaf.bookingEase.dtos.*;
import com.munaf.bookingEase.entities.*;
import com.munaf.bookingEase.entities.enums.BookingStatus;
import com.munaf.bookingEase.exceptions.BookingExpiredException;
import com.munaf.bookingEase.exceptions.InvalidInputException;
import com.munaf.bookingEase.exceptions.ResourceNotFoundException;
import com.munaf.bookingEase.exceptions.UnAuthorisedException;
import com.munaf.bookingEase.priceStrategy.service.PricingService;
import com.munaf.bookingEase.repositories.*;
import com.munaf.bookingEase.services.BookingService;
import com.munaf.bookingEase.services.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import static com.munaf.bookingEase.utils.UserUtils.getCurrentUser;

@Service
@Slf4j
public class BookingServiceIMPL implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;
    private final ModelMapper modelMapper;

    @Value("${frontend.url}")
    private String frontendUrl;

    public BookingServiceIMPL(BookingRepository bookingRepository, HotelRepository hotelRepository, RoomRepository roomRepository, InventoryRepository inventoryRepository, GuestRepository guestRepository, CheckoutService checkoutService, PricingService pricingService, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.inventoryRepository = inventoryRepository;
        this.guestRepository = guestRepository;
        this.checkoutService = checkoutService;
        this.pricingService = pricingService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest bookingRequest) {
        User user = getCurrentUser();
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + bookingRequest.getHotelId()));

        Room room = roomRepository.findByIdAndHotel_Id(bookingRequest.getRoomId(), bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Room Not Found With Id : " + bookingRequest.getRoomId() + " For Hotel Id : " + bookingRequest.getHotelId()));

        List<Inventory> inventories = inventoryRepository.findAndLockAvailableInventory(bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getNumberOfRooms());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;
        if (inventories.size() != daysCount) {
            throw new InvalidInputException("Rooms Not Available For The Given Date Range");
        }

        // RESERVE THE ROOMS / UPDATE NUMBER OF ROOMS IN INVENTORIES
        inventoryRepository.initBooking(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getNumberOfRooms());

        // CREATE A BOOKING
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventories); // amount for 1 room
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getNumberOfRooms())); // amount for total room

        Booking booking = Booking.builder()
                .hotel(hotel)
                .room(room)
                .user(user)
                .numberOfRooms(bookingRequest.getNumberOfRooms())
                .bookingStatus(BookingStatus.RESERVED)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .amount(totalPrice)
                .guests(new HashSet<>())  // guests are not added yet
                .build();

        booking = bookingRepository.save(booking);
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        bookingDto.setHotelDto(modelMapper.map(booking.getHotel(), HotelDto.class));
        bookingDto.setRoomDto(modelMapper.map(booking.getRoom(), RoomDto.class));
        bookingDto.setGuestDtos(new HashSet<>()); // // guests are not added yet
        bookingDto.setUserDto(modelMapper.map(user, UserDto.class));

        return bookingDto;
    }

    @Override
    @Transactional(noRollbackFor = BookingExpiredException.class)
    public BookingDto addGuestsToBooking(Long bookingId, List<GuestDto> guestDtoList) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found With Id : " + bookingId));
        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) throw new UnAuthorisedException("Booking Does Not Belongs To This User With Id : " + user.getId());
        if (hasBookingExpired(booking.getCreatedAt())) {
            inventoryRepository.expireBooking(booking.getRoom().getId(),
                            booking.getCheckInDate(),
                            booking.getCheckOutDate(),
                            booking.getNumberOfRooms()
            );
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new BookingExpiredException("Booking Has Been Already Expired");
        }
        if (guestDtoList.size() > booking.getRoom().getCapacity()) throw new InvalidInputException("Guests Size Is Greater Than Room Capacity");
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
        bookingDto.setHotelDto(modelMapper.map(booking.getHotel(), HotelDto.class));
        bookingDto.setRoomDto(modelMapper.map(booking.getRoom(), RoomDto.class));
        bookingDto.setUserDto(userDto);
        bookingDto.setGuestDtos(new HashSet<>(guestDtos));

        return bookingDto;
    }

    @Override
    @Transactional(noRollbackFor = BookingExpiredException.class)
    public String initiateBookingPayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found With Id : " + bookingId));

        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) throw new UnAuthorisedException("Booking Does Not Belongs To This User With Id : " + user.getId());
        if (hasBookingExpired(booking.getCreatedAt())) {
            inventoryRepository.expireBooking(booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getNumberOfRooms()
            );
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new BookingExpiredException("Booking Has Been Already Expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking, frontendUrl + "/payment/success", frontendUrl + "/payment/failure"); // payment url
        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking =
                    bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                            new ResourceNotFoundException("Booking not found for session Id: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            List<Inventory> lockReservedInventory = inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getNumberOfRooms());

            // INCREASE BOOKED COUNT AND DECREASE RESERVED COUNT
            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getNumberOfRooms());

            log.info("Successfully confirmed the booking for Booking Id: {}", booking.getId());
        } else {
            log.warn("Unhandled event type: {}", event.getType());
        }
    }


    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: "+bookingId));

        User user = getCurrentUser();
        if (!user.equals(booking.getUser())) throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED) throw new IllegalStateException("Only confirmed bookings can be cancelled");

        booking.setBookingStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);

        List<Inventory> lockReservedInventory = inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getNumberOfRooms());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getNumberOfRooms());

        // handle the refund

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookingStatus getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking Not Found With Id : " + bookingId));
        User user = getCurrentUser();

        if (!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus();
    }


    public Boolean hasBookingExpired(LocalDateTime createdAt) {
        return createdAt.plusMinutes(10).isBefore(LocalDateTime.now()); // valid for 10 minutes
    }


    // @Scheduled(cron = "*/5 * * * * *") // EVERY 5 SECONDS
    @Scheduled(cron = "0 */15 * * * *") // every 15 mins
    public void expireBooking() {
        int pageNo = 0;
        int pageSize = 100;

        while (true) {
            Page<Booking> bookingPage = bookingRepository.findAll(PageRequest.of(pageNo, pageSize));
            if (bookingPage.isEmpty()) break; // if all records are done then exit loop

            for (Booking booking : bookingPage.getContent()) {
                if (hasBookingExpired(booking.getCreatedAt()) && !EnumSet.of(BookingStatus.CONFIRMED, BookingStatus.CANCELED, BookingStatus.EXPIRED).contains(booking.getBookingStatus())) {
                    expireBookingStatus(booking);
                }

            }
            pageNo++;
        }
    }

    public void expireBookingStatus(Booking booking) {
        inventoryRepository.expireBooking(booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getNumberOfRooms()
        );
        booking.setBookingStatus(BookingStatus.EXPIRED);
        bookingRepository.save(booking);
    }

}
