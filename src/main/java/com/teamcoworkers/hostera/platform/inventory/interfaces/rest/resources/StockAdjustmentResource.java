package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "StockAdjustment", description = "Recorded movement of stock, in or out of a storage location")
public record StockAdjustmentResource(
        @Schema(example = "opening-1-1") String id,
        @Schema(example = "in", allowableValues = {"in", "out"}) String operation,
        @Schema(example = "60") BigDecimal quantity,
        @Schema(example = "1") Long locationId,
        @Schema(example = "Housekeeping") String locationName,
        @Schema(example = "opening-stock") String reason,
        @Schema(example = "") String note,
        @Schema(example = "2026-10-04T10:00:00Z") String recordedAt,
        @Schema(example = "Demo operator") String operator,
        @Schema(example = "null", description = "Identifier shared by the two adjustments of a transfer") String transferId) {
}
