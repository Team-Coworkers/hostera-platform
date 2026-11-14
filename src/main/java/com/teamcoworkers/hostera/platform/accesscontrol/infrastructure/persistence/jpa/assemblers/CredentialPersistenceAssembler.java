package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.CredentialType;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.Revocation;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.RevocationReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.StaffScope;
import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.entities.CredentialPersistenceEntity;

public final class CredentialPersistenceAssembler {

    private CredentialPersistenceAssembler() {
    }

    public static Credential toDomainFromPersistence(CredentialPersistenceEntity entity) {
        var revocation = entity.getRevokedAt() == null ? null : new Revocation(entity.getRevokedAt(),
                entity.getRevokedBy(), entity.getRevocationReason() == null ? null
                : RevocationReason.fromValue(entity.getRevocationReason()), entity.getRevocationNote());
        return Credential.reconstitute(entity.getId(), entity.getPropertyId(), entity.getCardId(),
                CredentialType.fromValue(entity.getType()), entity.getHolderName(), entity.getStaffMemberId(),
                entity.getBookingId(), entity.getBookingCode(), entity.getRoomId(),
                entity.getScope() == null ? null : StaffScope.fromValue(entity.getScope()), entity.getValidFrom(),
                entity.getValidUntil(), entity.getIssuedAt(), entity.getIssuedBy(), revocation);
    }

    public static CredentialPersistenceEntity toPersistenceFromDomain(Credential credential) {
        var entity = new CredentialPersistenceEntity();
        entity.setId(credential.getId());
        entity.setPropertyId(credential.getPropertyId());
        entity.setCardId(credential.getCardId());
        entity.setType(credential.getType().value());
        entity.setHolderName(credential.getHolderName());
        entity.setStaffMemberId(credential.getStaffMemberId());
        entity.setBookingId(credential.getBookingId());
        entity.setBookingCode(credential.getBookingCode());
        entity.setRoomId(credential.getRoomId());
        entity.setScope(credential.getScope() == null ? null : credential.getScope().value());
        entity.setValidFrom(credential.getValidFrom());
        entity.setValidUntil(credential.getValidUntil());
        entity.setIssuedAt(credential.getIssuedAt());
        entity.setIssuedBy(credential.getIssuedBy());
        var revocation = credential.getRevocation();
        if (revocation != null) {
            entity.setRevokedAt(revocation.revokedAt());
            entity.setRevokedBy(revocation.revokedBy());
            entity.setRevocationReason(revocation.reason().value());
            entity.setRevocationNote(revocation.note());
        } else {
            entity.setRevocationNote("");
        }
        return entity;
    }
}
