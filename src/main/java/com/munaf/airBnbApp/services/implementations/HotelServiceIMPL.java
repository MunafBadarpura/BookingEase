package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.dtos.HotelDto;
import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.exceptions.ResourceNotFoundException;
import com.munaf.airBnbApp.repositories.HotelRepository;
import com.munaf.airBnbApp.services.HotelService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HotelServiceIMPL implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    public HotelServiceIMPL(HotelRepository hotelRepository, ModelMapper modelMapper) {
        this.hotelRepository = hotelRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name : {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        log.info("Created a new hotel with Id : {}", hotelDto.getId());
        return modelMapper.map(hotelRepository.save(hotel), HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long hotelId) {
        log.info("Getting a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long hotelId, HotelDto updateHotelDto) {
        log.info("Updating a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        Boolean active = hotel.getActive();
        modelMapper.map(updateHotelDto, hotel);
        hotel.setId(hotelId);
        hotel.setActive(active);
        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public Boolean deleteHotelById(Long hotelId) {
        log.info("Deleting a hotel with Id : {}", hotelId);
        if (!hotelRepository.existsById(hotelId)) throw new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId);
        hotelRepository.deleteById(hotelId);
        return true;
    }

    @Override
    public HotelDto activateHotel(Long hotelId) {
        log.info("Activating a hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not Found With Id : " + hotelId));
        hotel.setActive(true);
        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }
}
