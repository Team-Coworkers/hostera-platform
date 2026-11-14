package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Status of a credential at a moment, derived from its validity and its revocation. */
public enum CredentialStatus {
    ACTIVE("active"),
    SCHEDULED("scheduled"),
    EXPIRED("expired"),
    REVOKED("revoked"),
    LOST("lost");

    private final String value;

    CredentialStatus(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static CredentialStatus fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown credentialStatus: " + value));
    }
}
