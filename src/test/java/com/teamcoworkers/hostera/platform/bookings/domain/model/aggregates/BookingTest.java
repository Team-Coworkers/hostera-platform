package com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.bookings.domain.model.events.BookingClosedEvent;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingLifecycle;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.BookingStatus;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.StayPeriod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.teamcoworkers.hostera.platform.bookings.BookingFixtures.booking;
import static com.teamcoworkers.hostera.platform.bookings.BookingFixtures.guest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {

    private static final StayPeriod STAY = new StayPeriod(LocalDate.parse("2026-10-07"), LocalDate.parse("2026-10-09"));

    private static Booking newBooking(Integer guests, BigDecimal total) {
        return new Booking(1L, "BKG-1048", BookingStatus.CONFIRMED, guest(), STAY, guests, 1L, 12L, 1L, total, null,
                null, null);
    }

    @Test
    void createsAValidBookingWithDefaults() {
        var booking = newBooking(2, new BigDecimal("370"));

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getGuestRequest()).isEmpty();
        assertThat(booking.getCreatedAt()).isNotNull();
        assertThat(booking.getLifecycle()).isEqualTo(BookingLifecycle.empty());
    }

    @Test
    void rejectsABookingWithoutGuests() {
        assertThatThrownBy(() -> newBooking(0, new BigDecimal("370")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one guest");
    }

    @Test
    void rejectsANegativeTotal() {
        assertThatThrownBy(() -> newBooking(2, new BigDecimal("-1"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void conflictsWithAnotherActiveBookingOfTheSameRoomAndNights() {
        var booking = booking(1L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09");

        assertThat(booking.conflictsWith(booking(2L, BookingStatus.PENDING, 12L, "2026-10-08", "2026-10-10"))).isTrue();
        assertThat(booking.conflictsWith(booking(2L, BookingStatus.PENDING, 14L, "2026-10-08", "2026-10-10"))).isFalse();
        assertThat(booking.conflictsWith(booking(2L, BookingStatus.CANCELLED, 12L, "2026-10-08", "2026-10-10"))).isFalse();
        assertThat(booking.conflictsWith(booking(2L, BookingStatus.CONFIRMED, 12L, "2026-10-09", "2026-10-11"))).isFalse();
        assertThat(booking.conflictsWith(booking(1L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09"))).isFalse();
    }

    @Test
    void registersABookingClosedEventWhenTheGuestChecksOut() {
        var booking = booking(7L, BookingStatus.CHECKED_IN, 12L, "2026-10-07", "2026-10-09");

        booking.updateInformation(1L, booking.getCode(), BookingStatus.CHECKED_OUT, booking.getGuest(),
                booking.getStay(), 2, 1L, 12L, 1L, booking.getTotalAmount(), "", booking.getLifecycle());

        assertThat(booking.domainEvents()).singleElement()
                .isInstanceOfSatisfying(BookingClosedEvent.class, event -> {
                    assertThat(event.bookingId()).isEqualTo(7L);
                    assertThat(event.status()).isEqualTo("checked-out");
                });
    }

    @Test
    void doesNotRegisterAnEventForChangesThatKeepTheBookingActive() {
        var booking = booking(7L, BookingStatus.CONFIRMED, 12L, "2026-10-07", "2026-10-09");

        booking.updateInformation(1L, booking.getCode(), BookingStatus.CHECKED_IN, booking.getGuest(),
                booking.getStay(), 2, 1L, 12L, 1L, booking.getTotalAmount(), "", booking.getLifecycle());

        assertThat(booking.domainEvents()).isEmpty();
    }
}
