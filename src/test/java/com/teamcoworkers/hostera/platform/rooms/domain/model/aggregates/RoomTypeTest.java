package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomTypeTest {
    private static final NightlyRate RATE = new NightlyRate(new BigDecimal("180"));

    @Test
    void createsARoomTypeWithTrimmedValues() {
        var roomType = new RoomType(1L, " Standard ", 2, " 1 queen ", RATE, ActivationStatus.ACTIVE);
        assertThat(roomType.getName()).isEqualTo("Standard");
        assertThat(roomType.getBedConfiguration()).isEqualTo("1 queen");
        assertThat(roomType.isActive()).isTrue();
    }

    @Test
    void rejectsACapacityOutOfRange() {
        assertThatThrownBy(() -> new RoomType(1L, "Standard", 0, "1 queen", RATE, ActivationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RoomType(1L, "Standard", RoomType.MAX_CAPACITY + 1, "1 queen", RATE, ActivationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsABaseNightlyRateThatIsNotGreaterThanZero() {
        assertThatThrownBy(() -> new NightlyRate(BigDecimal.ZERO)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NightlyRate(new BigDecimal("-10"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RoomType(1L, "Standard", 2, "1 queen", null, ActivationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
