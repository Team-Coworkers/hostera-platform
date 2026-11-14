package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateCredentialCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CredentialData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.UpdateCredentialCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.CredentialResource;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.SaveCredentialResource;

import java.time.Instant;

public final class CredentialResourceFromEntityAssembler {

    private CredentialResourceFromEntityAssembler() {
    }

    public static CredentialResource toResourceFromEntity(Credential credential) {
        var revocation = credential.getRevocation();
        return new CredentialResource(credential.getId(), credential.getPropertyId(), credential.getCardId(),
                credential.getType().value(), credential.getHolderName(), credential.getStaffMemberId(),
                credential.getBookingId(), credential.getBookingCode(), credential.getRoomId(),
                credential.getScope() == null ? null : credential.getScope().value(), credential.getValidFrom(),
                credential.getValidUntil(), credential.getIssuedAt(), credential.getIssuedBy(),
                revocation == null ? null : revocation.revokedAt(),
                revocation == null ? null : revocation.revokedBy(),
                revocation == null ? null : revocation.reason().value(),
                revocation == null ? "" : revocation.note(),
                credential.statusAt(Instant.now()).value());
    }

    public static CreateCredentialCommand toCreateCommand(SaveCredentialResource resource) {
        return new CreateCredentialCommand(toData(resource));
    }

    public static UpdateCredentialCommand toUpdateCommand(Long credentialId, SaveCredentialResource resource) {
        return new UpdateCredentialCommand(credentialId, toData(resource));
    }

    private static CredentialData toData(SaveCredentialResource resource) {
        return new CredentialData(resource.propertyId(), resource.cardId(), resource.type(), resource.holderName(),
                resource.staffMemberId(), resource.bookingId(), resource.bookingCode(), resource.roomId(),
                resource.scope(), resource.validFrom(), resource.validUntil(), resource.issuedAt(),
                resource.issuedBy(), resource.revokedAt(), resource.revokedBy(), resource.revocationReason(),
                resource.revocationNote());
    }
}
