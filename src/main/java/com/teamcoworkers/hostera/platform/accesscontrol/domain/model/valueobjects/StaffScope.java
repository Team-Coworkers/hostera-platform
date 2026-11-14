package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Areas a staff credential can open. */
public enum StaffScope {
    SERVICE_AREAS("service-areas"),
    ALL_ROOMS("all-rooms"),
    ALL_AREAS("all-areas");

    private final String value;

    StaffScope(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static StaffScope fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown staffScope: " + value));
    }
}
