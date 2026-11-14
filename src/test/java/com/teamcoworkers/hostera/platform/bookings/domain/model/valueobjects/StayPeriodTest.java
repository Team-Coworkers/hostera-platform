package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StayPeriodTest {

    private static StayPeriod stay(String checkIn, String checkOut) {
        return new StayPeriod(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
    }

    @Test
    void rejectsACheckOutThatIsNotAfterTheCheckIn() {
        assertThatThrownBy(() -> stay("2026-10-09", "2026-10-09")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> stay("2026-10-09", "2026-10-07")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsMissingDates() {
        assertThatThrownBy(() -> new StayPeriod(null, LocalDate.parse("2026-10-09")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void overlapsOnlyWhenTheStaysShareANight() {
        var stay = stay("2026-10-07", "2026-10-09");

        assertThat(stay.overlaps(stay("2026-10-08", "2026-10-10"))).isTrue();
        assertThat(stay.overlaps(stay("2026-10-05", "2026-10-12"))).isTrue();
        assertThat(stay.overlaps(stay("2026-10-09", "2026-10-11"))).isFalse();
        assertThat(stay.overlaps(stay("2026-10-04", "2026-10-07"))).isFalse();
    }
}
