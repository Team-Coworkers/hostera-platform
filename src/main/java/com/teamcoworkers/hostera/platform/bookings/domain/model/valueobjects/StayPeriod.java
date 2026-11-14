package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

import java.time.LocalDate;

/**
 * Nights a booking covers, from the check-in date to the check-out date.
 * <p>
 * Invariant: both dates are present and the check-out date is after the check-in date, so a stay
 * always covers at least one night. The check-out day itself is free for the next guest.
 * </p>
 */
public record StayPeriod(LocalDate checkInDate, LocalDate checkOutDate) {

    public StayPeriod {
        if (checkInDate == null) throw new IllegalArgumentException("Check-in date is required");
        if (checkOutDate == null) throw new IllegalArgumentException("Check-out date is required");
        if (!checkOutDate.isAfter(checkInDate))
            throw new IllegalArgumentException("Check-out date must be after the check-in date");
    }

    /** @return whether the two stays share at least one night */
    public boolean overlaps(StayPeriod other) {
        return checkInDate.isBefore(other.checkOutDate) && other.checkInDate.isBefore(checkOutDate);
    }

    /** @return whether the given day falls inside the stay, check-out day excluded */
    public boolean includes(LocalDate day) {
        return !day.isBefore(checkInDate) && day.isBefore(checkOutDate);
    }
}
