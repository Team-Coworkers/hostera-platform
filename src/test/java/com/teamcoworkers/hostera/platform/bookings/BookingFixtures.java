package com.teamcoworkers.hostera.platform.bookings;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingLifecycle;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.Guest;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.StayPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Builds bookings for the tests of the bookings context. */
public final class BookingFixtures {

    private BookingFixtures() {
    }

    public static Booking booking(Long id, BookingStatus status, Long roomId, String checkIn, String checkOut) {
        return Booking.reconstitute(id, 1L, "BKG-" + (1000 + (id == null ? 0 : id)), status, guest(),
                new StayPeriod(LocalDate.parse(checkIn), LocalDate.parse(checkOut)), 2, 1L, roomId, 1L,
                new BigDecimal("340"), "", LocalDate.parse("2026-10-01"), BookingLifecycle.empty());
    }

    public static Guest guest() {
        return new Guest("Adriana Torres", "adriana.torres@example.com", "+51 910 100 200", "es", null, "");
    }
}
