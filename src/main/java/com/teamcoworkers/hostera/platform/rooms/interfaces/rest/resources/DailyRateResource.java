package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "DailyRate", description = "Price of one night of a room type under a rate plan")
public record DailyRateResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "1") Long ratePlanId,
        @Schema(example = "1") Long roomTypeId,
        @Schema(example = "2026-10-01") String date,
        @Schema(example = "190") BigDecimal amount) {
}
