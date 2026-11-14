package com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects;

/**
 * Operational condition that a status period records for a room on a range of days.
 * A blocked or out-of-service room cannot be booked; a room that needs cleaning can.
 */
public enum OperationalStatus {
    BLOCKED("blocked"),
    OUT_OF_SERVICE("out-of-service"),
    NEEDS_CLEANING("needs-cleaning");

    private final String value;

    OperationalStatus(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    /** @return whether a room in this condition cannot be booked nor given to a guest */
    public boolean preventsBooking() {
        return this != NEEDS_CLEANING;
    }

    public static OperationalStatus fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Status must be one of: blocked, out-of-service, needs-cleaning");
    }
}
