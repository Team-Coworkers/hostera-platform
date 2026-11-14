package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "InventoryItem", description = "Supply of a property with its stock per storage location")
public record InventoryItemResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "Bath towels") String name,
        @Schema(example = "LIN-BT-001") String code,
        @Schema(example = "linen") String category,
        @Schema(example = "units", allowableValues = {"units", "liters", "kilograms"}) String unit,
        @Schema(example = "1") Long primaryLocationId,
        @Schema(example = "30") BigDecimal lowStockThreshold,
        List<StockResource> stocks,
        List<StockAdjustmentResource> adjustments) {
}
