package com.munaf.airBnbApp.priceStrategy;

import com.munaf.airBnbApp.entities.Inventory;

import java.math.BigDecimal;

public class SurgePricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    public SurgePricingStrategy(PricingStrategy wrapped) {
        this.wrapped = wrapped;
    }


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
