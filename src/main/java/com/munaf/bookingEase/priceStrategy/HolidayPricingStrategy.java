package com.munaf.bookingEase.priceStrategy;

import com.munaf.bookingEase.entities.Inventory;

import java.math.BigDecimal;

public class HolidayPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    public HolidayPricingStrategy(PricingStrategy wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        boolean isHoliday = true; // TODO: get from external API
        if (isHoliday) price = price.multiply(BigDecimal.valueOf(1.25));

        return price;
    }
}
