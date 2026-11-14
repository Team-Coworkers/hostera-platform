package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.DateRange;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.OperationalStatus;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.StatusPeriodPersistenceEntity;

public final class StatusPeriodPersistenceAssembler {
    private StatusPeriodPersistenceAssembler() {
    }

    public static StatusPeriod toDomainFromPersistence(StatusPeriodPersistenceEntity entity) {
        if (entity == null) return null;
        return StatusPeriod.reconstitute(entity.getId(), entity.getPropertyId(), entity.getRoomId(),
                OperationalStatus.fromValue(entity.getStatus()), new DateRange(entity.getStartDate(), entity.getEndDate()),
                entity.getReason());
    }

    public static StatusPeriodPersistenceEntity toPersistenceFromDomain(StatusPeriod statusPeriod) {
        var entity = new StatusPeriodPersistenceEntity();
        if (statusPeriod.getId() != null) entity.setId(statusPeriod.getId());
        entity.setPropertyId(statusPeriod.getPropertyId());
        entity.setRoomId(statusPeriod.getRoomId());
        entity.setStatus(statusPeriod.getStatus().value());
        entity.setStartDate(statusPeriod.getPeriod().startDate());
        entity.setEndDate(statusPeriod.getPeriod().endDate());
        entity.setReason(statusPeriod.getReason());
        return entity;
    }
}
