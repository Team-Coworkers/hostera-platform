package com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Price of one night in the currency of the property.
 *
 * @param amount an amount greater than zero
 */
public record NightlyRate(BigDecimal amount) {
    public NightlyRate {
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Nightly rate must be greater than zero");
    }
}
