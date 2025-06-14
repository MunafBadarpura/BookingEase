package com.munaf.bookingEase.priceStrategy;

import com.munaf.bookingEase.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
