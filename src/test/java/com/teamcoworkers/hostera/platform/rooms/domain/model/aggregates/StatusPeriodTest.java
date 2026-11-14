package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.DateRange;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.OperationalStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusPeriodTest {

    @Test
    void rejectsAnEndDateBeforeTheStartDate() {
        assertThatThrownBy(() -> DateRange.of("2026-10-11", "2026-10-10")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsAPeriodOfOneDay() {
        var period = DateRange.of("2026-10-10", "2026-10-10");
        assertThat(period.covers(LocalDate.of(2026, 10, 10))).isTrue();
        assertThat(period.covers(LocalDate.of(2026, 10, 11))).isFalse();
    }

    @Test
    void rejectsADateThatIsNotACalendarDay() {
        assertThatThrownBy(() -> DateRange.of("10/10/2026", "2026-10-11")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aBlockedPeriodPreventsBookingButACleaningPeriodDoesNot() {
        var period = DateRange.of("2026-10-10", "2026-10-11");
        var blocked = new StatusPeriod(1L, 12L, OperationalStatus.BLOCKED, period, "Held for a corporate group");
        var cleaning = new StatusPeriod(1L, 12L, OperationalStatus.NEEDS_CLEANING, period, "Deep cleaning after checkout");

        assertThat(blocked.preventsBookingOn(LocalDate.of(2026, 10, 10))).isTrue();
        assertThat(blocked.preventsBookingOn(LocalDate.of(2026, 10, 12))).isFalse();
        assertThat(cleaning.preventsBookingOn(LocalDate.of(2026, 10, 10))).isFalse();
    }

    @Test
    void rejectsABlankReason() {
        var period = DateRange.of("2026-10-10", "2026-10-11");
        assertThatThrownBy(() -> new StatusPeriod(1L, 12L, OperationalStatus.BLOCKED, period, " "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
