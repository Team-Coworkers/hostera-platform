package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

import java.util.Arrays;

/** How a payment was collected, with the same kebab-case values as the frontend. */
public enum PaymentMethod {
    CASH("cash"),
    CARD_TERMINAL("card-terminal"),
    BANK_TRANSFER("bank-transfer");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Payment method is required");
        return Arrays.stream(values())
                .filter(method -> method.value.equals(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Payment method must be one of cash, card-terminal or bank-transfer"));
    }
}
