package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects;

import java.time.Instant;

/** When, by whom and why a credential was revoked. A credential that has one cannot open anything. */
public record Revocation(Instant revokedAt, String revokedBy, RevocationReason reason, String note) {

    public Revocation {
        if (revokedAt == null) throw new IllegalArgumentException("Revocation moment is required");
        if (reason == null) reason = RevocationReason.OTHER;
        if (revokedBy == null) revokedBy = "";
        if (note == null) note = "";
    }
}
