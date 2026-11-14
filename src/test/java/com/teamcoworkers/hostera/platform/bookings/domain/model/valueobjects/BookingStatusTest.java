package com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingStatusTest {

    @Test
    void readsTheValuesTheFrontendSends() {
        assertThat(BookingStatus.fromValue("checked-in")).isEqualTo(BookingStatus.CHECKED_IN);
        assertThat(BookingStatus.fromValue("no-show")).isEqualTo(BookingStatus.NO_SHOW);
        assertThat(BookingStatus.CHECKED_OUT.value()).isEqualTo("checked-out");
    }

    @Test
    void rejectsAnUnknownStatus() {
        assertThatThrownBy(() -> BookingStatus.fromValue("archived")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void onlyPendingConfirmedAndCheckedInHoldTheRoom() {
        assertThat(BookingStatus.PENDING.isActive()).isTrue();
        assertThat(BookingStatus.CONFIRMED.isActive()).isTrue();
        assertThat(BookingStatus.CHECKED_IN.isActive()).isTrue();
        assertThat(BookingStatus.CANCELLED.isActive()).isFalse();
        assertThat(BookingStatus.NO_SHOW.isActive()).isFalse();
        assertThat(BookingStatus.CHECKED_OUT.isActive()).isFalse();
    }
}
