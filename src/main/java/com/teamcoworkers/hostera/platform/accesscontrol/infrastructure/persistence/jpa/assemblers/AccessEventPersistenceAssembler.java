package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessPoint;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessResult;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.DenialReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.HolderType;
import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.entities.AccessEventPersistenceEntity;

public final class AccessEventPersistenceAssembler {

    private AccessEventPersistenceAssembler() {
    }

    public static AccessEvent toDomainFromPersistence(AccessEventPersistenceEntity entity) {
        return AccessEvent.reconstitute(entity.getId(), entity.getPropertyId(), entity.getOccurredAt(),
                entity.getCredentialId(), entity.getCardId(), entity.getHolderName(),
                entity.getHolderType() == null ? null : HolderType.fromValue(entity.getHolderType()),
                AccessPoint.fromValue(entity.getAccessPoint()), entity.getRoomId(),
                AccessResult.fromValue(entity.getResult()),
                entity.getDenialReason() == null ? null : DenialReason.fromValue(entity.getDenialReason()));
    }

    public static AccessEventPersistenceEntity toPersistenceFromDomain(AccessEvent event) {
        var entity = new AccessEventPersistenceEntity();
        entity.setId(event.getId());
        entity.setPropertyId(event.getPropertyId());
        entity.setOccurredAt(event.getOccurredAt());
        entity.setCredentialId(event.getCredentialId());
        entity.setCardId(event.getCardId());
        entity.setHolderName(event.getHolderName());
        entity.setHolderType(event.getHolderType() == null ? null : event.getHolderType().value());
        entity.setAccessPoint(event.getAccessPoint().value());
        entity.setRoomId(event.getRoomId());
        entity.setResult(event.getResult().value());
        entity.setDenialReason(event.getDenialReason() == null ? null : event.getDenialReason().value());
        return entity;
    }
}
