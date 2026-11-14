package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.IncludedServices;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RatePlanTest {

    @Test
    void aPlanWithoutRoomTypesAppliesToAllOfThem() {
        var ratePlan = new RatePlan(1L, "Flexible", List.of(), IncludedServices.ROOM_ONLY, true,
                "Free cancellation until 24 hours before arrival", ActivationStatus.ACTIVE);
        assertThat(ratePlan.appliesTo(4L)).isTrue();
    }

    @Test
    void aPlanWithRoomTypesOnlyAppliesToThem() {
        var ratePlan = new RatePlan(1L, "Honeymoon", List.of(4L), IncludedServices.HALF_BOARD, true,
                "Free cancellation until 7 days before arrival", ActivationStatus.ACTIVE);
        assertThat(ratePlan.appliesTo(4L)).isTrue();
        assertThat(ratePlan.appliesTo(1L)).isFalse();
    }

    @Test
    void aRefundablePlanNeedsACancellationPolicy() {
        assertThatThrownBy(() -> new RatePlan(1L, "Flexible", List.of(), IncludedServices.ROOM_ONLY, true, " ", ActivationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aNonRefundablePlanKeepsNoCancellationPolicy() {
        var ratePlan = new RatePlan(1L, "Non-refundable", List.of(), IncludedServices.ROOM_ONLY, false,
                "Free cancellation until 24 hours before arrival", ActivationStatus.ACTIVE);
        assertThat(ratePlan.isRefundable()).isFalse();
        assertThat(ratePlan.getCancellationPolicy()).isEmpty();
    }

    @Test
    void rejectsARepeatedRoomType() {
        assertThatThrownBy(() -> new RatePlan(1L, "Summer", List.of(1L, 1L), IncludedServices.ROOM_ONLY, false, "", ActivationStatus.ACTIVE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
