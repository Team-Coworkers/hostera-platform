package com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects;

/**
 * Meal services that a rate plan includes in the nightly price.
 */
public enum IncludedServices {
    ROOM_ONLY("room-only"),
    BREAKFAST_INCLUDED("breakfast-included"),
    HALF_BOARD("half-board"),
    FULL_BOARD("full-board");

    private final String value;

    IncludedServices(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    public static IncludedServices fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Included services must be one of: room-only, breakfast-included, half-board, full-board");
    }
}
