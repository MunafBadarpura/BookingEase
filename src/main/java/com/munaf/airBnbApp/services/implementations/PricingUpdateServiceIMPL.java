package com.munaf.airBnbApp.services.implementations;

import com.munaf.airBnbApp.entities.Hotel;
import com.munaf.airBnbApp.entities.HotelMinPrice;
import com.munaf.airBnbApp.entities.Inventory;
import com.munaf.airBnbApp.priceStrategy.service.PricingService;
import com.munaf.airBnbApp.repositories.HotelMinPriceRepository;
import com.munaf.airBnbApp.repositories.HotelRepository;
import com.munaf.airBnbApp.repositories.InventoryRepository;
import com.munaf.airBnbApp.services.PricingUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class PricingUpdateServiceIMPL implements PricingUpdateService {

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    public PricingUpdateServiceIMPL(HotelRepository hotelRepository, InventoryRepository inventoryRepository, HotelMinPriceRepository hotelMinPriceRepository, PricingService pricingService) {
        this.hotelRepository = hotelRepository;
        this.inventoryRepository = inventoryRepository;
        this.hotelMinPriceRepository = hotelMinPriceRepository;
        this.pricingService = pricingService;
    }

    // Schedular to update the Inventory and HotelMinPrice every 1 Hour
    // @Scheduled(cron = "*/5 * * * * *") // EVERY 5 SECONDS
    // @Scheduled(cron = "0 */5 * * * *")// EVERY 5 MIN
    @Scheduled(cron = "0 0 * * * *") // EVERY HOUR
    public void updatePrices() {
        int pageNo = 0;
        int pageSize = 100;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(pageNo, pageSize));
            if (hotelPage.isEmpty()) break; // if all records are done then exit loop

            hotelPage.getContent().forEach(hotel -> updateHotelPrices(hotel));

            pageNo++;
        }


    }

    private void updateHotelPrices(Hotel hotel) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventories = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        // UPDATE INVENTORIES PRICE
        inventories.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventories);

        // UPDATE HOTEL MIN PRICE
        Map<LocalDate, BigDecimal> dailyMinPrices = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);

    }
}
