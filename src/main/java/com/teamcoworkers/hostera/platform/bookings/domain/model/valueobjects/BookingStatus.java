package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

import java.util.Arrays;

/**
 * Lifecycle state of a booking, written in the API with the same kebab-case values as the frontend.
 * <p>
 * Pending, confirmed and checked-in bookings are active: they hold their room for the stay.
 * Cancelled, no-show and checked-out bookings are closed and release it.
 * </p>
 */
public enum BookingStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    CHECKED_IN("checked-in"),
    CHECKED_OUT("checked-out"),
    CANCELLED("cancelled"),
    NO_SHOW("no-show");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /** @return whether the booking still holds its room for the stay */
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == CHECKED_IN;
    }

    /** @return whether the stay ended in a way that must close the guest's access */
    public boolean endsGuestAccess() {
        return this == CHECKED_OUT || this == CANCELLED;
    }

    public static BookingStatus fromValue(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Booking status is required");
        return Arrays.stream(values())
                .filter(status -> status.value.equals(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Booking status must be one of pending, confirmed, checked-in, checked-out, cancelled or no-show"));
    }
}
