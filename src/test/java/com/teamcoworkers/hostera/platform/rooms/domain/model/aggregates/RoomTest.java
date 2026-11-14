package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomTest {

    @Test
    void aNewRoomStartsAvailableWithATrimmedNumber() {
        var room = new Room(1L, " 201 ", 1L, 2);
        assertThat(room.getNumber()).isEqualTo("201");
        assertThat(room.getStatus()).isEqualTo(RoomStatus.AVAILABLE);
        assertThat(room.canReceiveGuests()).isTrue();
    }

    @Test
    void rejectsABlankNumberOrAMissingRoomType() {
        assertThatThrownBy(() -> new Room(1L, " ", 1L, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Room(1L, "201", null, 2)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void afterTheCheckOutTheRoomNeedsCleaningAndIsNotReady() {
        var room = new Room(1L, "201", 1L, 2).checkIn();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.OCCUPIED);

        room.checkOut();

        assertThat(room.getStatus()).isEqualTo(RoomStatus.NEEDS_CLEANING);
        assertThat(room.canReceiveGuests()).isFalse();
    }

    @Test
    void anOccupiedRoomCannotBecomeReadyOrAvailableWithoutCleaning() {
        var room = new Room(1L, "201", 1L, 2).checkIn();
        assertThatThrownBy(room::markAsReady).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(room::release).isInstanceOf(IllegalStateException.class);
        assertThat(room.getStatus()).isEqualTo(RoomStatus.OCCUPIED);
    }

    @Test
    void aCleanedRoomIsReadyForTheNextArrival() {
        var room = new Room(1L, "201", 1L, 2).checkIn().checkOut().markAsReady();
        assertThat(room.getStatus()).isEqualTo(RoomStatus.READY);
        assertThat(room.canReceiveGuests()).isTrue();
    }

    @Test
    void aBlockedRoomCannotReceiveGuestsUntilItIsReleased() {
        var room = new Room(1L, "201", 1L, 2).block();
        assertThat(room.canReceiveGuests()).isFalse();
        assertThatThrownBy(room::checkIn).isInstanceOf(IllegalStateException.class);

        room.release();

        assertThat(room.canReceiveGuests()).isTrue();
    }
}
