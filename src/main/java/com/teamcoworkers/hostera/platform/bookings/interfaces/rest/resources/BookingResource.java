package com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** A booking with the same fields and names the frontend reads from the API. */
@Schema(name = "Booking", description = "Guest stay at a property, from the request to the check-out")
public record BookingResource(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long propertyId,
        @Schema(example = "BKG-1048") String code,
        @Schema(example = "confirmed") String status,
        @Schema(example = "Adriana Torres") String guestName,
        @Schema(example = "adriana.torres@example.com") String guestEmail,
        @Schema(example = "+51 910 100 200") String guestPhone,
        @Schema(example = "es") String preferredLanguage,
        @Schema(example = "2026-10-07") LocalDate checkInDate,
        @Schema(example = "2026-10-09") LocalDate checkOutDate,
        @Schema(example = "2") Integer guests,
        @Schema(example = "1") Long roomTypeId,
        @Schema(example = "12") Long roomId,
        @Schema(example = "1") Long ratePlanId,
        @Schema(example = "340") BigDecimal totalAmount,
        @Schema(example = "Late arrival, around 22:00") String guestRequest,
        @Schema(example = "2026-10-01") LocalDate createdAt,
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
