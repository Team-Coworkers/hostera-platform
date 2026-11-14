package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities.StorageLocationPersistenceEntity;

public final class StorageLocationPersistenceAssembler {
    private StorageLocationPersistenceAssembler() {
    }

    public static StorageLocation toDomainFromPersistence(StorageLocationPersistenceEntity entity) {
        if (entity == null) return null;
        return StorageLocation.reconstitute(entity.getId(), entity.getPropertyId(), entity.getName(), entity.getCode(),
                StorageLocationType.fromValue(entity.getType()), entity.getArea(), entity.getResponsibleTeam(),
                entity.getDescription());
    }

    public static StorageLocationPersistenceEntity toPersistenceFromDomain(StorageLocation storageLocation) {
        var entity = new StorageLocationPersistenceEntity();
        if (storageLocation.getId() != null) entity.setId(storageLocation.getId());
        entity.setPropertyId(storageLocation.getPropertyId());
        entity.setName(storageLocation.getName());
        entity.setCode(storageLocation.getCode());
        entity.setType(storageLocation.getType().value());
        entity.setArea(storageLocation.getArea());
        entity.setResponsibleTeam(storageLocation.getResponsibleTeam());
        entity.setDescription(storageLocation.getDescription());
        return entity;
    }
}
