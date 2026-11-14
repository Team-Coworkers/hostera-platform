package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.RoomTypePersistenceEntity;

public final class RoomTypePersistenceAssembler {
    private RoomTypePersistenceAssembler() {
    }

    public static RoomType toDomainFromPersistence(RoomTypePersistenceEntity entity) {
        if (entity == null) return null;
        return RoomType.reconstitute(entity.getId(), entity.getPropertyId(), entity.getName(), entity.getCapacity(),
                entity.getBedConfiguration(), new NightlyRate(entity.getBaseNightlyRate()),
                ActivationStatus.fromValue(entity.getStatus()));
    }

    public static RoomTypePersistenceEntity toPersistenceFromDomain(RoomType roomType) {
        var entity = new RoomTypePersistenceEntity();
        if (roomType.getId() != null) entity.setId(roomType.getId());
        entity.setPropertyId(roomType.getPropertyId());
        entity.setName(roomType.getName());
        entity.setCapacity(roomType.getCapacity());
        entity.setBedConfiguration(roomType.getBedConfiguration());
        entity.setBaseNightlyRate(roomType.getBaseNightlyRate().amount());
        entity.setStatus(roomType.getStatus().value());
        return entity;
    }
}
