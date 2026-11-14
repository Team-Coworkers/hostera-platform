package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "RoomType", description = "Category of rooms of a property")
public record RoomTypeResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "Standard") String name,
        @Schema(example = "2") Integer capacity,
        @Schema(example = "1 queen") String bedConfiguration,
        @Schema(example = "180") BigDecimal baseNightlyRate,
        @Schema(example = "active", allowableValues = {"active", "inactive"}) String status) {
}
