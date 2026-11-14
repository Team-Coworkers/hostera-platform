package com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects;

import java.util.Locale;

/**
 * ISO 4217 currency code in which a property charges its stays (for example, PEN).
 *
 * @param code three uppercase letters
 */
public record CurrencyCode(String code) {
    public CurrencyCode {
        if (code == null || !code.trim().matches("[A-Za-z]{3}"))
            throw new IllegalArgumentException("Currency code must have three letters");
        code = code.trim().toUpperCase(Locale.ROOT);
    }
}
