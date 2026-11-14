package com.teamcoworkers.hostera.platform.inventory.application.commands;

import java.math.BigDecimal;
import java.util.List;

public record UpdateInventoryItemCommand(Long inventoryItemId, Long propertyId, String name, String code, String category, String unit, Long primaryLocationId, BigDecimal lowStockThreshold, List<StockItem> stocks, List<StockAdjustmentItem> adjustments) {
}
