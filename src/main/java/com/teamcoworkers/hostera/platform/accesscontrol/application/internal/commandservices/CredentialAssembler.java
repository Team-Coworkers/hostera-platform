package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CredentialData;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.CredentialType;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.Revocation;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.RevocationReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.StaffScope;

/** Turns the credential fields of the frontend into the domain model. */
public final class CredentialAssembler {

    private CredentialAssembler() {
    }

    public static Credential toCredential(CredentialData data) {
        return new Credential(data.propertyId(), data.cardId(), CredentialType.fromValue(data.type()),
                data.holderName(), data.staffMemberId(), data.bookingId(), data.bookingCode(), data.roomId(),
                toScope(data.scope()), data.validFrom(), data.validUntil(), data.issuedAt(), data.issuedBy(),
                toRevocation(data));
    }

    public static Credential update(Credential credential, CredentialData data) {
        return credential.updateInformation(data.propertyId(), data.cardId(), CredentialType.fromValue(data.type()),
                data.holderName(), data.staffMemberId(), data.bookingId(), data.bookingCode(), data.roomId(),
                toScope(data.scope()), data.validFrom(), data.validUntil(), data.issuedAt(), data.issuedBy(),
                toRevocation(data));
    }

    private static StaffScope toScope(String scope) {
        return scope == null || scope.isBlank() ? null : StaffScope.fromValue(scope);
    }

    private static Revocation toRevocation(CredentialData data) {
        if (data.revokedAt() == null) return null;
        var reason = data.revocationReason() == null ? null : RevocationReason.fromValue(data.revocationReason());
        return new Revocation(data.revokedAt(), data.revokedBy(), reason, data.revocationNote());
    }
}
