package com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Payload to create or replace a booking. The frontend sends the whole booking on every change of
 * its lifecycle, so the lifecycle fields are accepted too.
 */
@Schema(name = "SaveBookingRequest", description = "Payload to create or update a booking")
public record SaveBookingResource(
        @NotNull @Positive @Schema(example = "1") Long propertyId,
        @NotBlank @Schema(example = "BKG-1048") String code,
        @NotBlank @Schema(example = "confirmed") String status,
        @NotBlank @Schema(example = "Adriana Torres") String guestName,
        @Email @Schema(example = "adriana.torres@example.com") String guestEmail,
        @Schema(example = "+51 910 100 200") String guestPhone,
        @Schema(example = "es") String preferredLanguage,
        @NotNull @Schema(example = "2026-10-07") LocalDate checkInDate,
        @NotNull @Schema(example = "2026-10-09") LocalDate checkOutDate,
        @NotNull @Min(1) @Schema(example = "2") Integer guests,
        @Positive @Schema(example = "1") Long roomTypeId,
        @Positive @Schema(example = "12") Long roomId,
        @Positive @Schema(example = "1") Long ratePlanId,
        @NotNull @PositiveOrZero @Schema(example = "340") BigDecimal totalAmount,
        @Schema(example = "Late arrival, around 22:00") String guestRequest,
        @Schema(example = "2026-10-01T15:20:00Z") Instant createdAt,
        @Schema(example = "2026-10-01T15:25:00Z") Instant confirmedAt,
        @Schema(example = "Demo operator") String confirmedBy,
        @Schema(nullable = true) Instant cancelledAt,
        @Schema(nullable = true) String cancelledBy,
        @Schema(nullable = true) String cancellationReason,
        @Schema(example = "") String cancellationNote,
        @Schema(nullable = true) Instant noShowAt,
        @Schema(nullable = true) String noShowBy,
        @Schema(nullable = true) Instant checkedInAt,
        @Schema(nullable = true) String checkedInBy,
        @Schema(nullable = true) String guestDocumentType,
        @Schema(example = "") String guestDocumentNumber,
        @Schema(nullable = true) Instant checkedOutAt,
        @Schema(nullable = true) String checkedOutBy,
        @Schema(nullable = true) String roomCondition,
        @Schema(example = "") String departureNote) {
}
