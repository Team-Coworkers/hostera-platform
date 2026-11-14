package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatusPeriod", description = "Range of days in which a room has an operational status")
public record StatusPeriodResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "12") Long roomId,
        @Schema(example = "blocked", allowableValues = {"blocked", "out-of-service", "needs-cleaning"}) String status,
        @Schema(example = "2026-10-10") String startDate,
        @Schema(example = "2026-10-11") String endDate,
        @Schema(example = "Held for a corporate group") String reason) {
}
