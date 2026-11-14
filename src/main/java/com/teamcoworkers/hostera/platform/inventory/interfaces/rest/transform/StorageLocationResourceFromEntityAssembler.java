package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.StorageLocationResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.SaveStorageLocationResource;

public final class StorageLocationResourceFromEntityAssembler {
    private StorageLocationResourceFromEntityAssembler() {
    }

    public static StorageLocationResource toResourceFromEntity(StorageLocation storageLocation) {
        return new StorageLocationResource(storageLocation.getId(),
                storageLocation.getPropertyId(),
                storageLocation.getName(),
                storageLocation.getCode(),
                storageLocation.getType().value(),
                storageLocation.getArea(),
                storageLocation.getResponsibleTeam(),
                storageLocation.getDescription());
    }

    public static CreateStorageLocationCommand toCreateCommand(SaveStorageLocationResource resource) {
        return new CreateStorageLocationCommand(resource.propertyId(), resource.name(), resource.code(), resource.type(), resource.area(), resource.responsibleTeam(), resource.description());
    }

    public static UpdateStorageLocationCommand toUpdateCommand(Long storageLocationId, SaveStorageLocationResource resource) {
        return new UpdateStorageLocationCommand(storageLocationId, resource.propertyId(), resource.name(), resource.code(), resource.type(), resource.area(), resource.responsibleTeam(), resource.description());
    }
}
