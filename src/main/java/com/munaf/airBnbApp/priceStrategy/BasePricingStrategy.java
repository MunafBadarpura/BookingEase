package com.munaf.airBnbApp.priceStrategy;

import com.munaf.airBnbApp.entities.Inventory;

import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
