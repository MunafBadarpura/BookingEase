package com.munaf.airBnbApp.priceStrategy;

import com.munaf.airBnbApp.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
