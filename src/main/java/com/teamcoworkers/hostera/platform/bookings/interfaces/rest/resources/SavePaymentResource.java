package com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(name = "SavePaymentRequest", description = "Payload to record or update a payment")
public record SavePaymentResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotNull @Positive @Schema(example = "2") Long bookingId,
        @NotNull @Positive @Schema(example = "255") BigDecimal amount,
        @NotBlank @Schema(example = "card-terminal") String method,
        @Schema(example = "2026-10-05T16:30:00Z") Instant paidAt,
        @Schema(example = "POS-470075") String reference,
        @Schema(example = "Demo operator") String recordedBy) {
}
