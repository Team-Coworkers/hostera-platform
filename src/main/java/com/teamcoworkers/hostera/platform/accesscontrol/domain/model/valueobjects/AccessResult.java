package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Outcome of an access attempt. */
public enum AccessResult {
    GRANTED("granted"),
    DENIED("denied");

    private final String value;

    AccessResult(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static AccessResult fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown accessResult: " + value));
    }
}
