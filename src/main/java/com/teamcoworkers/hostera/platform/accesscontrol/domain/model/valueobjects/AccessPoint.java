package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Readers a credential can be presented to. */
public enum AccessPoint {
    MAIN_ENTRANCE("main-entrance"),
    ELEVATOR("elevator"),
    ROOM_DOOR("room-door"),
    SERVICE_CORRIDOR("service-corridor"),
    STORAGE_ROOM("storage-room");

    private final String value;

    AccessPoint(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static AccessPoint fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown accessPoint: " + value));
    }
}
