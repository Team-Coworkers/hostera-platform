package com.teamcoworkers.hostera.platform.bookings.domain.model.events;

import java.time.Instant;

/**
 * An active booking was checked out or cancelled. Access control revokes its key cards and the
 * room of a checked-out booking moves to needs-cleaning.
 */
public record BookingClosedEvent(Long bookingId, Long propertyId, Long roomId, String status, Instant closedAt) {
}
