package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Why an access attempt was denied. */
public enum DenialReason {
    REVOKED("revoked"),
    NOT_ACTIVE_YET("not-active-yet"),
    EXPIRED("expired"),
    OUTSIDE_SCOPE("outside-scope");

    private final String value;

    DenialReason(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static DenialReason fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown denialReason: " + value));
    }
}
