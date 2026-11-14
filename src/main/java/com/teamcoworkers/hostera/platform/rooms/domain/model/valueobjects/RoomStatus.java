package com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects;

/**
 * Current housekeeping and occupancy state of a room.
 * <p>
 * The states are different on purpose: {@code available} is a free room, {@code ready} is a room
 * already cleaned and inspected for the next arrival, {@code occupied} has a guest in house,
 * {@code needs-cleaning} is waiting for housekeeping and {@code blocked} is out of sale.
 * </p>
 * <p>
 * Transition rules: an occupied room can only go to needs-cleaning (the check-out never leaves it
 * ready), a room that needs cleaning can only become ready or blocked, and a blocked room has to
 * be released or cleaned before a guest can use it.
 * </p>
 */
public enum RoomStatus {
    AVAILABLE("available"),
    READY("ready"),
    OCCUPIED("occupied"),
    NEEDS_CLEANING("needs-cleaning"),
    BLOCKED("blocked");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    /** @return whether a room in this state can change to the target state */
    public boolean canChangeTo(RoomStatus target) {
        if (target == null) return false;
        if (this == target) return true;
        return switch (this) {
            case AVAILABLE, READY -> true;
            case OCCUPIED -> target == NEEDS_CLEANING;
            case NEEDS_CLEANING -> target == READY || target == BLOCKED;
            case BLOCKED -> target == AVAILABLE || target == NEEDS_CLEANING;
        };
    }

    /** @return whether a guest can check in to a room in this state */
    public boolean canReceiveGuests() {
        return this == AVAILABLE || this == READY;
    }

    public static RoomStatus fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Room status must be one of: available, ready, occupied, needs-cleaning, blocked");
    }
}
