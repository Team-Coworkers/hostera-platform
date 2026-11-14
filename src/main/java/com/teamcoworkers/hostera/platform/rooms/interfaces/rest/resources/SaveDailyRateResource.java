package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(name = "SaveDailyRateRequest", description = "Payload to create or update a daily rate")
public record SaveDailyRateResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotNull @Positive @Schema(example = "1") Long ratePlanId,
        @NotNull @Positive @Schema(example = "1") Long roomTypeId,
        @NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") @Schema(example = "2026-10-01") String date,
        @NotNull @Positive @Schema(example = "190") BigDecimal amount) {
}
