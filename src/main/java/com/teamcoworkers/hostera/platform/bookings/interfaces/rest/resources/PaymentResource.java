package com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(name = "Payment", description = "Money collected for a booking")
public record PaymentResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "2") Long bookingId,
        @Schema(example = "255") BigDecimal amount,
        @Schema(example = "card-terminal") String method,
        @Schema(example = "2026-10-05T16:30:00Z") Instant paidAt,
        @Schema(example = "POS-470075") String reference,
        @Schema(example = "Demo operator") String recordedBy) {
}
