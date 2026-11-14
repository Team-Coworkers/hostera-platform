package com.teamcoworkers.hostera.platform.accesscontrol;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.CredentialType;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.Revocation;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.StaffScope;

import java.time.Instant;

/** Builds credentials for the tests of the access control context. */
public final class CredentialFixtures {

    public static final Instant FROM = Instant.parse("2026-10-03T20:26:00Z");
    public static final Instant UNTIL = Instant.parse("2026-10-07T16:00:00Z");
    public static final Instant DURING = Instant.parse("2026-10-05T03:40:00Z");

    private CredentialFixtures() {
    }

    public static Credential guestCard(Long id, Long roomId, Revocation revocation) {
        return Credential.reconstitute(id, 1L, "8F2A", CredentialType.GUEST_KEY_CARD, "Elena Ferrer", null, 3L,
                "BKG-1032", roomId, null, FROM, UNTIL, FROM, "Demo operator", revocation);
    }

    public static Credential staffCredential(Long id, StaffScope scope) {
        return Credential.reconstitute(id, 1L, "7E44", CredentialType.STAFF_CREDENTIAL, "Rosa Pérez", 3L, null, "",
                null, scope, Instant.parse("2026-09-01T13:00:00Z"), null, null, "Demo operator", null);
    }
}
