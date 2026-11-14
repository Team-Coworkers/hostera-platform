package com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.*;
import org.junit.jupiter.api.Test;

import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.DURING;
import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.guestCard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccessEventTest {

    @Test
    void recordsAnAttemptAtAnotherRoomAsDenied() {
        var event = AccessEvent.recordAttempt(guestCard(1L, 6L, null), AccessPoint.ROOM_DOOR, 9L, DURING);

        assertThat(event.getResult()).isEqualTo(AccessResult.DENIED);
        assertThat(event.getDenialReason()).isEqualTo(DenialReason.OUTSIDE_SCOPE);
        assertThat(event.getHolderType()).isEqualTo(HolderType.GUEST);
        assertThat(event.getCardId()).isEqualTo("8F2A");
        assertThat(event.getCredentialId()).isEqualTo(1L);
    }

    @Test
    void recordsAnAttemptWithARevokedCardAsDenied() {
        var revoked = guestCard(1L, 6L, new Revocation(DURING.minusSeconds(60), "Demo operator", RevocationReason.LOST_CARD, ""));

        var event = AccessEvent.recordAttempt(revoked, AccessPoint.ROOM_DOOR, 6L, DURING);

        assertThat(event.getResult()).isEqualTo(AccessResult.DENIED);
        assertThat(event.getDenialReason()).isEqualTo(DenialReason.REVOKED);
    }

    @Test
    void aDeniedAttemptNeedsItsReason() {
        assertThatThrownBy(() -> new AccessEvent(1L, DURING, 1L, "8F2A", "Elena Ferrer", HolderType.GUEST,
                AccessPoint.ELEVATOR, null, AccessResult.DENIED, null)).isInstanceOf(IllegalArgumentException.class);
    }
}
