package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

import java.time.Instant;

/**
 * Who moved a booking through each step of its lifecycle and when, plus the notes taken on the way.
 * Every field is optional: a step that has not happened stays empty.
 */
public record BookingLifecycle(
        Instant confirmedAt, String confirmedBy,
        Instant cancelledAt, String cancelledBy, String cancellationReason, String cancellationNote,
        Instant noShowAt, String noShowBy,
        Instant checkedInAt, String checkedInBy,
        Instant checkedOutAt, String checkedOutBy, String roomCondition, String departureNote) {

    public BookingLifecycle {
        cancellationNote = cancellationNote == null ? "" : cancellationNote;
        departureNote = departureNote == null ? "" : departureNote;
    }

    public static BookingLifecycle empty() {
        return new BookingLifecycle(null, null, null, null, null, null, null, null,
                null, null, null, null, null, null);
    }
}
