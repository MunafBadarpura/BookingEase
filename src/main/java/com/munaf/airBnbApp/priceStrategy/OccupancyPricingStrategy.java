package com.munaf.airBnbApp.priceStrategy;

import com.munaf.airBnbApp.entities.Inventory;

import java.math.BigDecimal;

public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    public OccupancyPricingStrategy(PricingStrategy wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        double occupancyRate = (double) inventory.getBookedCount() /  inventory.getTotalCount();
        if (occupancyRate > 0.8) price = price.multiply(BigDecimal.valueOf(1.2));

        return price;
    }
}
