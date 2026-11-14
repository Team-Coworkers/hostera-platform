package com.teamcoworkers.hostera.platform.inventory.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.StockAdjustmentItem;
import com.teamcoworkers.hostera.platform.inventory.application.commands.StockItem;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commandservices.InventoryItemCommandService;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.InventoryItemRepository;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.StorageLocationRepository;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryItemCommandServiceImpl implements InventoryItemCommandService {
    private final InventoryItemRepository inventoryItemRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public InventoryItemCommandServiceImpl(InventoryItemRepository inventoryItemRepository,
                                           StorageLocationRepository storageLocationRepository,
                                           OrganizationsContextFacade organizationsContextFacade) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.storageLocationRepository = storageLocationRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<InventoryItem, ApplicationError> handle(CreateInventoryItemCommand command) {
        if (!organizationsContextFacade.existsPropertyById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        if (command.code() != null && inventoryItemRepository.existsByPropertyIdAndCode(command.propertyId(), command.code().trim()))
            return Result.failure(ApplicationError.conflict("Inventory_Item", "Code '%s' already exists in the property".formatted(command.code())));
        try {
            var inventoryItem = new InventoryItem(command.propertyId(), command.name(), command.code(), command.category(),
                    InventoryUnit.fromValue(command.unit()), command.primaryLocationId(), command.lowStockThreshold(),
                    toStocks(command.stocks()), toAdjustments(command.adjustments()));
            var locationError = findLocationError(inventoryItem);
            if (locationError.isPresent()) return Result.failure(locationError.get());
            return Result.success(inventoryItemRepository.save(inventoryItem));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("inventoryItem", e.getMessage()));
        }
    }

    @Override
    public Result<InventoryItem, ApplicationError> handle(UpdateInventoryItemCommand command) {
        var existing = inventoryItemRepository.findById(command.inventoryItemId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Inventory_Item", command.inventoryItemId().toString()));
        if (!organizationsContextFacade.existsPropertyById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        if (command.code() != null && inventoryItemRepository.existsByPropertyIdAndCodeAndIdIsNot(command.propertyId(), command.code().trim(), command.inventoryItemId()))
            return Result.failure(ApplicationError.conflict("Inventory_Item", "Code '%s' already exists in the property".formatted(command.code())));
        try {
            var inventoryItem = existing.get().updateInformation(command.propertyId(), command.name(), command.code(),
                    command.category(), InventoryUnit.fromValue(command.unit()), command.primaryLocationId(),
                    command.lowStockThreshold());
            inventoryItem.replaceStock(
                    command.stocks() == null ? inventoryItem.getStocks() : toStocks(command.stocks()),
                    command.adjustments() == null ? inventoryItem.getAdjustments() : toAdjustments(command.adjustments()));
            var locationError = findLocationError(inventoryItem);
            if (locationError.isPresent()) return Result.failure(locationError.get());
            return Result.success(inventoryItemRepository.save(inventoryItem));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("inventoryItem", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("stock-adjustments-are-append-only", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteInventoryItemCommand command) {
        var existing = inventoryItemRepository.findById(command.inventoryItemId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Inventory_Item", command.inventoryItemId().toString()));
        if (existing.get().totalQuantity().signum() > 0)
            return Result.failure(ApplicationError.businessRuleViolation("inventory-item-has-stock",
                    "Record the stock that goes out before deleting the item"));
        inventoryItemRepository.deleteById(command.inventoryItemId());
        return Result.success(command.inventoryItemId());
    }

    /** The primary location and the locations of the stocks belong to the property of the item. */
    private Optional<ApplicationError> findLocationError(InventoryItem inventoryItem) {
        var locationIds = storageLocationRepository.findAllByPropertyId(inventoryItem.getPropertyId()).stream()
                .map(StorageLocation::getId).collect(Collectors.toSet());
        if (!locationIds.contains(inventoryItem.getPrimaryLocationId()))
            return Optional.of(ApplicationError.notFound("Storage_Location", String.valueOf(inventoryItem.getPrimaryLocationId())));
        for (var stock : inventoryItem.getStocks())
            if (!locationIds.contains(stock.locationId()))
                return Optional.of(ApplicationError.notFound("Storage_Location", String.valueOf(stock.locationId())));
        return Optional.empty();
    }

    private static List<Stock> toStocks(List<StockItem> items) {
        if (items == null) return List.of();
        return items.stream().filter(Objects::nonNull)
                .map(item -> new Stock(item.locationId(), item.quantity())).toList();
    }

    private static List<StockAdjustment> toAdjustments(List<StockAdjustmentItem> items) {
        if (items == null) return List.of();
        return items.stream().filter(Objects::nonNull)
                .map(item -> new StockAdjustment(item.id(), StockOperation.fromValue(item.operation()), item.quantity(),
                        item.locationId(), item.locationName(), item.reason(), item.note(), toInstant(item.recordedAt()),
                        item.operator(), item.transferId())).toList();
    }

    private static Instant toInstant(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Stock adjustment date is required");
        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Stock adjustment date must be an ISO-8601 instant, for example 2026-10-04T10:00:00Z");
        }
    }
}
