package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Reasons to revoke a credential. */
public enum RevocationReason {
    LOST_CARD("lost-card"),
    DAMAGED_CARD("damaged-card"),
    SECURITY_RISK("security-risk"),
    STAFF_LEFT("staff-left"),
    REPLACED("replaced"),
    OTHER("other");

    private final String value;

    RevocationReason(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static RevocationReason fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown revocationReason: " + value));
    }
}
