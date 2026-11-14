package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.StockAdjustmentItem;
import com.teamcoworkers.hostera.platform.inventory.application.commands.StockItem;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.InventoryItemResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.SaveInventoryItemResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.StockAdjustmentResource;
import com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources.StockResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class InventoryItemResourceFromEntityAssembler {
    private InventoryItemResourceFromEntityAssembler() {
    }

    public static InventoryItemResource toResourceFromEntity(InventoryItem inventoryItem) {
        return new InventoryItemResource(inventoryItem.getId(), inventoryItem.getPropertyId(), inventoryItem.getName(),
                inventoryItem.getCode(), inventoryItem.getCategory(), inventoryItem.getUnit().value(),
                inventoryItem.getPrimaryLocationId(), plain(inventoryItem.getLowStockThreshold()),
                inventoryItem.getStocks().stream().map(InventoryItemResourceFromEntityAssembler::toStockResource).toList(),
                inventoryItem.getAdjustments().stream().map(InventoryItemResourceFromEntityAssembler::toAdjustmentResource).toList());
    }

    public static CreateInventoryItemCommand toCreateCommand(SaveInventoryItemResource resource) {
        return new CreateInventoryItemCommand(resource.propertyId(), resource.name(), resource.code(), resource.category(),
                resource.unit(), resource.primaryLocationId(), resource.lowStockThreshold(),
                toStockItems(resource.stocks()), toAdjustmentItems(resource.adjustments()));
    }

    public static UpdateInventoryItemCommand toUpdateCommand(Long inventoryItemId, SaveInventoryItemResource resource) {
        return new UpdateInventoryItemCommand(inventoryItemId, resource.propertyId(), resource.name(), resource.code(),
                resource.category(), resource.unit(), resource.primaryLocationId(), resource.lowStockThreshold(),
                toStockItems(resource.stocks()), toAdjustmentItems(resource.adjustments()));
    }

    private static StockResource toStockResource(Stock stock) {
        return new StockResource(stock.locationId(), plain(stock.quantity()));
    }

    private static StockAdjustmentResource toAdjustmentResource(StockAdjustment adjustment) {
        return new StockAdjustmentResource(adjustment.id(), adjustment.operation().value(), plain(adjustment.quantity()),
                adjustment.locationId(), adjustment.locationName(), adjustment.reason(), adjustment.note(),
                adjustment.recordedAt().toString(), adjustment.operator(), adjustment.transferId());
    }

    /** A list that the request does not send stays {@code null}, so an update keeps the current one. */
    private static List<StockItem> toStockItems(List<StockResource> stocks) {
        if (stocks == null) return null;
        return stocks.stream().filter(Objects::nonNull)
                .map(stock -> new StockItem(stock.locationId(), stock.quantity())).toList();
    }

    private static List<StockAdjustmentItem> toAdjustmentItems(List<StockAdjustmentResource> adjustments) {
        if (adjustments == null) return null;
        return adjustments.stream().filter(Objects::nonNull)
                .map(adjustment -> new StockAdjustmentItem(adjustment.id(), adjustment.operation(), adjustment.quantity(),
                        adjustment.locationId(), adjustment.locationName(), adjustment.reason(), adjustment.note(),
                        adjustment.recordedAt(), adjustment.operator(), adjustment.transferId())).toList();
    }

    /** Writes 60 instead of 60.000 and 12.5 instead of 12.500, as the frontend sends them. */
    private static BigDecimal plain(BigDecimal value) {
        var stripped = value.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }
}
