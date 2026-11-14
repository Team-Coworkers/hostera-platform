package com.teamcoworkers.hostera.platform.bookings.application.commands;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Every field a booking is created or replaced with. The frontend sends the whole booking on each
 * change of its lifecycle, so create and update share this payload.
 */
public record BookingData(
        Long propertyId, String code, String status,
        String guestName, String guestEmail, String guestPhone, String preferredLanguage,
        String guestDocumentType, String guestDocumentNumber,
        LocalDate checkInDate, LocalDate checkOutDate, Integer guests,
        Long roomTypeId, Long roomId, Long ratePlanId, BigDecimal totalAmount, String guestRequest,
        LocalDate createdAt,
        Instant confirmedAt, String confirmedBy,
        Instant cancelledAt, String cancelledBy, String cancellationReason, String cancellationNote,
        Instant noShowAt, String noShowBy,
        Instant checkedInAt, String checkedInBy,
        Instant checkedOutAt, String checkedOutBy, String roomCondition, String departureNote) {
}
