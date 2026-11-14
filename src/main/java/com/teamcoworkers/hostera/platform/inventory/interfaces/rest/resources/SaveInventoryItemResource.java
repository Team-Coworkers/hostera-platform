package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "SaveInventoryItemRequest", description = "Payload to create or update a inventory item")
public record SaveInventoryItemResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "Bath towels") String name,
        @NotBlank @Schema(example = "LIN-BT-001") String code,
        @NotBlank @Schema(example = "linen") String category,
        @NotBlank @Pattern(regexp = "units|liters|kilograms") @Schema(example = "units", allowableValues = {"units", "liters", "kilograms"}) String unit,
        @NotNull @Positive @Schema(example = "1") Long primaryLocationId,
        @NotNull @PositiveOrZero @Schema(example = "30") BigDecimal lowStockThreshold,
        @Schema(description = "Stock of the item per storage location") List<StockResource> stocks,
        @Schema(description = "History of stock adjustments; new ones go at the end") List<StockAdjustmentResource> adjustments) {
}
