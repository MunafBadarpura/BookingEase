package com.munaf.airBnbApp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class HotelInfoDto {

    private HotelDto hotelDto;
    private List<RoomDto> roomDtos;

}
