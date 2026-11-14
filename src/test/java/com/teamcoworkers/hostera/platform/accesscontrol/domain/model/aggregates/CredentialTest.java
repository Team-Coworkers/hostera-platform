package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CredentialTest {

    private static final Revocation LOST = new Revocation(DURING, "Demo operator", RevocationReason.LOST_CARD, "");

    @Test
    void derivesItsStatusFromItsValidityAndRevocation() {
        var card = guestCard(1L, 6L, null);

        assertThat(card.statusAt(Instant.parse("2026-10-01T00:00:00Z"))).isEqualTo(CredentialStatus.SCHEDULED);
        assertThat(card.statusAt(DURING)).isEqualTo(CredentialStatus.ACTIVE);
        assertThat(card.statusAt(UNTIL)).isEqualTo(CredentialStatus.EXPIRED);
        assertThat(guestCard(1L, 6L, LOST).statusAt(DURING)).isEqualTo(CredentialStatus.LOST);
        card.revoke(DURING, "Demo operator", RevocationReason.SECURITY_RISK, "");
        assertThat(card.statusAt(DURING)).isEqualTo(CredentialStatus.REVOKED);
    }

    @Test
    void aGuestKeyCardNeedsItsBookingAndRoom() {
        assertThatThrownBy(() -> new Credential(1L, "8F2A", CredentialType.GUEST_KEY_CARD, "Elena Ferrer", null, 3L,
                "BKG-1032", null, null, FROM, UNTIL, FROM, "Demo operator", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnEndBeforeTheStart() {
        assertThatThrownBy(() -> new Credential(1L, "8F2A", CredentialType.GUEST_KEY_CARD, "Elena Ferrer", null, 3L,
                "BKG-1032", 6L, null, UNTIL, FROM, FROM, "Demo operator", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void staysRevokedWhenAnUpdateArrivesWithoutTheRevocation() {
        var card = guestCard(1L, 6L, LOST);

        card.updateInformation(1L, "8F2A", CredentialType.GUEST_KEY_CARD, "Elena Ferrer", null, 3L, "BKG-1032", 6L,
                null, FROM, Instant.parse("2026-10-06T10:00:00Z"), FROM, "Demo operator", null);

        assertThat(card.getRevocation()).isEqualTo(LOST);
        assertThat(card.revoke(DURING, "Hostera", RevocationReason.OTHER, "")).isFalse();
    }

    @Test
    void aGuestKeyCardOpensTheCommonAreasAndOnlyItsOwnRoom() {
        var card = guestCard(1L, 6L, null);

        assertThat(card.evaluateAccess(AccessPoint.MAIN_ENTRANCE, null, DURING)).isEqualTo(AccessDecision.granted());
        assertThat(card.evaluateAccess(AccessPoint.ELEVATOR, null, DURING)).isEqualTo(AccessDecision.granted());
        assertThat(card.evaluateAccess(AccessPoint.ROOM_DOOR, 6L, DURING)).isEqualTo(AccessDecision.granted());
        assertThat(card.evaluateAccess(AccessPoint.ROOM_DOOR, 7L, DURING))
                .isEqualTo(AccessDecision.denied(DenialReason.OUTSIDE_SCOPE));
        assertThat(card.evaluateAccess(AccessPoint.SERVICE_CORRIDOR, null, DURING))
                .isEqualTo(AccessDecision.denied(DenialReason.OUTSIDE_SCOPE));
    }

    @Test
    void aCredentialThatIsNotActiveIsDenied() {
        assertThat(guestCard(1L, 6L, LOST).evaluateAccess(AccessPoint.ROOM_DOOR, 6L, DURING))
                .isEqualTo(AccessDecision.denied(DenialReason.REVOKED));
        assertThat(guestCard(1L, 6L, null).evaluateAccess(AccessPoint.ROOM_DOOR, 6L, UNTIL))
                .isEqualTo(AccessDecision.denied(DenialReason.EXPIRED));
        assertThat(guestCard(1L, 6L, null).evaluateAccess(AccessPoint.ROOM_DOOR, 6L, Instant.parse("2026-10-01T00:00:00Z")))
                .isEqualTo(AccessDecision.denied(DenialReason.NOT_ACTIVE_YET));
    }

    @Test
    void aStaffCredentialOpensWhatItsScopeAllows() {
        var service = staffCredential(15L, StaffScope.SERVICE_AREAS);
        var rooms = staffCredential(16L, StaffScope.ALL_ROOMS);

        assertThat(service.evaluateAccess(AccessPoint.STORAGE_ROOM, null, DURING)).isEqualTo(AccessDecision.granted());
        assertThat(service.evaluateAccess(AccessPoint.ROOM_DOOR, 6L, DURING))
                .isEqualTo(AccessDecision.denied(DenialReason.OUTSIDE_SCOPE));
        assertThat(rooms.evaluateAccess(AccessPoint.ROOM_DOOR, 6L, DURING)).isEqualTo(AccessDecision.granted());
    }
}
