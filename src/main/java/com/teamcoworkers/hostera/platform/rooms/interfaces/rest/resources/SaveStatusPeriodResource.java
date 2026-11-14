package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(name = "SaveStatusPeriodRequest", description = "Payload to create or update a status period")
public record SaveStatusPeriodResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotNull @Positive @Schema(example = "12") Long roomId,
        @NotBlank @Pattern(regexp = "blocked|out-of-service|needs-cleaning") @Schema(example = "blocked", allowableValues = {"blocked", "out-of-service", "needs-cleaning"}) String status,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") @Schema(example = "2026-10-10") String startDate,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") @Schema(example = "2026-10-11") String endDate,
        @NotBlank @Schema(example = "Held for a corporate group") String reason) {
}
