package com.teamcoworkers.hostera.platform.inventory.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "Stock", description = "Quantity of an inventory item at one storage location")
public record StockResource(
        @Schema(example = "1") Long locationId,
        @Schema(example = "60") BigDecimal quantity) {
}
