package com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Quantity of an inventory item kept at one storage location.
 *
 * @param locationId storage location that holds the stock
 * @param quantity   a quantity that is never negative
 */
public record Stock(Long locationId, BigDecimal quantity) {
    public Stock {
        if (locationId == null || locationId <= 0)
            throw new IllegalArgumentException("Stock location id must be a positive number");
        if (quantity == null || quantity.signum() < 0)
            throw new IllegalArgumentException("Stock quantity cannot be negative");
    }
}
