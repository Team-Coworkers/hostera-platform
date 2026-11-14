package com.teamcoworkers.hostera.platform.bookings.domain.model.events;

import java.time.Instant;

/**
 * Published when an active booking is checked out or cancelled, so other contexts can end what
 * depended on the stay (for example, Access Control revokes the guest's key cards).
 *
 * @param status the closing status value, {@code checked-out} or {@code cancelled}
 */
public record BookingClosedEvent(Long bookingId, Long propertyId, String status, Instant closedAt) {
}
