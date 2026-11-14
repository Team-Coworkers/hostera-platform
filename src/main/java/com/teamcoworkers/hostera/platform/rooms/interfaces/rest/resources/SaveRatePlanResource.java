package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(name = "SaveRatePlanRequest", description = "Payload to create or update a rate plan")
public record SaveRatePlanResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "Flexible") String name,
        @Schema(example = "[]", description = "Room types that the plan prices. Empty means all of them.") List<Long> roomTypeIds,
        @NotBlank @Pattern(regexp = "room-only|breakfast-included|half-board|full-board") @Schema(example = "room-only", allowableValues = {"room-only", "breakfast-included", "half-board", "full-board"}) String includedServices,
        @NotNull @Schema(example = "true") Boolean refundable,
        @Schema(example = "Free cancellation until 24 hours before arrival") String cancellationPolicy,
        @NotBlank @Pattern(regexp = "active|inactive") @Schema(example = "active", allowableValues = {"active", "inactive"}) String status) {
}
