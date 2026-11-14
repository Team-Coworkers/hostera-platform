package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

/**
 * Person the booking is made for and the details the front desk needs to reach and identify them.
 * <p>
 * Invariant: the guest has a name. Contact and document details are optional until check-in.
 * </p>
 */
public record Guest(String name, String email, String phone, String preferredLanguage,
                    String documentType, String documentNumber) {

    public Guest {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Guest name is required");
        name = name.trim();
        email = blankToEmpty(email);
        phone = blankToEmpty(phone);
        preferredLanguage = preferredLanguage == null || preferredLanguage.isBlank() ? "es" : preferredLanguage.trim();
        documentNumber = blankToEmpty(documentNumber);
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
