package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "RatePlan", description = "Rate plan of a property")
public record RatePlanResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "Flexible") String name,
        @Schema(example = "[1, 2]") List<Long> roomTypeIds,
        @Schema(example = "room-only", allowableValues = {"room-only", "breakfast-included", "half-board", "full-board"}) String includedServices,
        @Schema(example = "true") boolean refundable,
        @Schema(example = "Free cancellation until 24 hours before arrival") String cancellationPolicy,
        @Schema(example = "active", allowableValues = {"active", "inactive"}) String status) {
}
