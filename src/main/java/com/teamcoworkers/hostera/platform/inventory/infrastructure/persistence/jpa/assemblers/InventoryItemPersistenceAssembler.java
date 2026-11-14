package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities.InventoryItemPersistenceEntity;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities.StockAdjustmentEmbeddable;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities.StockEmbeddable;

import java.util.ArrayList;

public final class InventoryItemPersistenceAssembler {
    private InventoryItemPersistenceAssembler() {
    }

    public static InventoryItem toDomainFromPersistence(InventoryItemPersistenceEntity entity) {
        if (entity == null) return null;
        var stocks = entity.getStocks().stream()
                .map(stock -> new Stock(stock.getLocationId(), stock.getQuantity())).toList();
        var adjustments = entity.getAdjustments().stream()
                .map(adjustment -> new StockAdjustment(adjustment.getAdjustmentCode(),
                        StockOperation.fromValue(adjustment.getOperation()), adjustment.getQuantity(),
                        adjustment.getLocationId(), adjustment.getLocationName(), adjustment.getReason(),
                        adjustment.getNote(), adjustment.getRecordedAt(), adjustment.getOperatorName(),
                        adjustment.getTransferCode())).toList();
        return InventoryItem.reconstitute(entity.getId(), entity.getPropertyId(), entity.getName(), entity.getCode(),
                entity.getCategory(), InventoryUnit.fromValue(entity.getUnit()), entity.getPrimaryLocationId(),
                entity.getLowStockThreshold(), stocks, adjustments);
    }

    public static InventoryItemPersistenceEntity toPersistenceFromDomain(InventoryItem inventoryItem) {
        var entity = new InventoryItemPersistenceEntity();
        if (inventoryItem.getId() != null) entity.setId(inventoryItem.getId());
        entity.setPropertyId(inventoryItem.getPropertyId());
        entity.setName(inventoryItem.getName());
        entity.setCode(inventoryItem.getCode());
        entity.setCategory(inventoryItem.getCategory());
        entity.setUnit(inventoryItem.getUnit().value());
        entity.setPrimaryLocationId(inventoryItem.getPrimaryLocationId());
        entity.setLowStockThreshold(inventoryItem.getLowStockThreshold());
        var stocks = new ArrayList<StockEmbeddable>();
        for (var stock : inventoryItem.getStocks()) stocks.add(toEmbeddable(stock));
        entity.setStocks(stocks);
        var adjustments = new ArrayList<StockAdjustmentEmbeddable>();
        for (var adjustment : inventoryItem.getAdjustments()) adjustments.add(toEmbeddable(adjustment));
        entity.setAdjustments(adjustments);
        return entity;
    }

    private static StockEmbeddable toEmbeddable(Stock stock) {
        var embeddable = new StockEmbeddable();
        embeddable.setLocationId(stock.locationId());
        embeddable.setQuantity(stock.quantity());
        return embeddable;
    }

    private static StockAdjustmentEmbeddable toEmbeddable(StockAdjustment adjustment) {
        var embeddable = new StockAdjustmentEmbeddable();
        embeddable.setAdjustmentCode(adjustment.id());
        embeddable.setOperation(adjustment.operation().value());
        embeddable.setQuantity(adjustment.quantity());
        embeddable.setLocationId(adjustment.locationId());
        embeddable.setLocationName(adjustment.locationName());
        embeddable.setReason(adjustment.reason());
        embeddable.setNote(adjustment.note());
        embeddable.setRecordedAt(adjustment.recordedAt());
        embeddable.setOperatorName(adjustment.operator());
        embeddable.setTransferCode(adjustment.transferId());
        return embeddable;
    }
}
