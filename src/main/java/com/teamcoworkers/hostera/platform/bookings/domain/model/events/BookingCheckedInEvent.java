package com.teamcoworkers.hostera.platform.bookings.domain.model.events;

import java.time.Instant;

/** The guest of a booking checked in, so its room is now occupied. */
public record BookingCheckedInEvent(Long bookingId, Long propertyId, Long roomId, Instant checkedInAt) {
}
