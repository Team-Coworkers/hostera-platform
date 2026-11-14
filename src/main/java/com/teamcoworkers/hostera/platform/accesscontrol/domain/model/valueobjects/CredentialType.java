package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.util.Arrays;

/** Kinds of credential: a key card for a guest or a credential for a staff member. */
public enum CredentialType {
    GUEST_KEY_CARD("guest-key-card"),
    STAFF_CREDENTIAL("staff-credential");

    private final String value;

    CredentialType(String value) {
        this.value = value;
    }

    /** @return the value the frontend sends and reads */
    public String value() {
        return value;
    }

    public static CredentialType fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown credentialType: " + value));
    }
}
