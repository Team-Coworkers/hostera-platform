package com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects;

/**
 * Whether a room type or a rate plan can be used in new bookings.
 */
public enum ActivationStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    ActivationStatus(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    public static ActivationStatus fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Status must be one of: active, inactive");
    }
}
