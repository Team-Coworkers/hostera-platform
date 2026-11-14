package com.teamcoworkers.hostera.platform.inventory.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commandservices.StorageLocationCommandService;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.InventoryItemRepository;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.StorageLocationRepository;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class StorageLocationCommandServiceImpl implements StorageLocationCommandService {
    private final StorageLocationRepository storageLocationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public StorageLocationCommandServiceImpl(StorageLocationRepository storageLocationRepository,
                                             InventoryItemRepository inventoryItemRepository,
                                             OrganizationsContextFacade organizationsContextFacade) {
        this.storageLocationRepository = storageLocationRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<StorageLocation, ApplicationError> handle(CreateStorageLocationCommand command) {
        if (!organizationsContextFacade.existsPropertyById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        if (command.code() != null && storageLocationRepository.existsByPropertyIdAndCode(command.propertyId(), command.code().trim()))
            return Result.failure(ApplicationError.conflict("Storage_Location", "Code '%s' already exists in the property".formatted(command.code())));
        try {
            var storageLocation = new StorageLocation(command.propertyId(), command.name(), command.code(),
                    StorageLocationType.fromValue(command.type()), command.area(), command.responsibleTeam(), command.description());
            return Result.success(storageLocationRepository.save(storageLocation));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("storageLocation", e.getMessage()));
        }
    }

    @Override
    public Result<StorageLocation, ApplicationError> handle(UpdateStorageLocationCommand command) {
        var existing = storageLocationRepository.findById(command.storageLocationId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Storage_Location", command.storageLocationId().toString()));
        if (!organizationsContextFacade.existsPropertyById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        if (command.code() != null && storageLocationRepository.existsByPropertyIdAndCodeAndIdIsNot(command.propertyId(), command.code().trim(), command.storageLocationId()))
            return Result.failure(ApplicationError.conflict("Storage_Location", "Code '%s' already exists in the property".formatted(command.code())));
        try {
            var storageLocation = existing.get().updateInformation(command.propertyId(), command.name(), command.code(),
                    StorageLocationType.fromValue(command.type()), command.area(), command.responsibleTeam(), command.description());
            return Result.success(storageLocationRepository.save(storageLocation));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("storageLocation", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteStorageLocationCommand command) {
        var existing = storageLocationRepository.findById(command.storageLocationId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Storage_Location", command.storageLocationId().toString()));
        var inUse = inventoryItemRepository.findAllByPropertyId(existing.get().getPropertyId()).stream()
                .anyMatch(inventoryItem -> inventoryItem.usesLocation(command.storageLocationId()));
        if (inUse)
            return Result.failure(ApplicationError.businessRuleViolation("storage-location-in-use",
                    "Move the stock and the primary location of its items to another storage location first"));
        storageLocationRepository.deleteById(command.storageLocationId());
        return Result.success(command.storageLocationId());
    }
}
