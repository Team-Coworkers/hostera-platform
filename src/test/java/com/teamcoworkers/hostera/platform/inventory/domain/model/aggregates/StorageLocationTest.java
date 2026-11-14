package com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StorageLocationTest {

    @Test
    void createsAStorageLocationWithTrimmedValues() {
        var location = new StorageLocation(1L, " Housekeeping ", " HK-01 ", StorageLocationType.WAREHOUSE, "Ground floor",
                "Housekeeping", null);
        assertThat(location.getName()).isEqualTo("Housekeeping");
        assertThat(location.getCode()).isEqualTo("HK-01");
        assertThat(location.getDescription()).isEmpty();
    }

    @Test
    void rejectsABlankCodeOrAMissingType() {
        assertThatThrownBy(() -> new StorageLocation(1L, "Housekeeping", " ", StorageLocationType.WAREHOUSE, "Ground floor", "Housekeeping", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new StorageLocation(1L, "Housekeeping", "HK-01", null, "Ground floor", "Housekeeping", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void readsTheTypeFromTheValueOfTheApi() {
        assertThat(StorageLocationType.fromValue("front-desk")).isEqualTo(StorageLocationType.FRONT_DESK);
        assertThatThrownBy(() -> StorageLocationType.fromValue("garage")).isInstanceOf(IllegalArgumentException.class);
    }
}
