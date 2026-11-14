package com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockCondition;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryItemTest {
    private static final Instant RECORDED_AT = Instant.parse("2026-10-04T10:00:00Z");

    private static Stock stock(Long locationId, String quantity) {
        return new Stock(locationId, new BigDecimal(quantity));
    }

    private static StockAdjustment adjustment(String id, StockOperation operation, String quantity, Long locationId) {
        return new StockAdjustment(id, operation, new BigDecimal(quantity), locationId, "Housekeeping", "opening-stock", "",
                RECORDED_AT, "Demo operator", null);
    }

    private static InventoryItem bathTowels(List<Stock> stocks, List<StockAdjustment> adjustments) {
        return new InventoryItem(1L, "Bath towels", "LIN-BT-001", "linen", InventoryUnit.UNITS, 1L, new BigDecimal("30"),
                stocks, adjustments);
    }

    @Test
    void addsTheStockOfEveryLocation() {
        var item = bathTowels(List.of(stock(1L, "60"), stock(3L, "24")), List.of());
        assertThat(item.totalQuantity()).isEqualByComparingTo("84");
        assertThat(item.quantityAt(3L)).isEqualByComparingTo("24");
        assertThat(item.quantityAt(9L)).isEqualByComparingTo("0");
        assertThat(item.stockCondition()).isEqualTo(StockCondition.IN_STOCK);
    }

    @Test
    void tellsWhenTheStockIsLowOrOut() {
        assertThat(bathTowels(List.of(stock(1L, "30")), List.of()).stockCondition()).isEqualTo(StockCondition.LOW_STOCK);
        assertThat(bathTowels(List.of(stock(1L, "0")), List.of()).stockCondition()).isEqualTo(StockCondition.OUT_OF_STOCK);
    }

    @Test
    void rejectsANegativeStock() {
        assertThatThrownBy(() -> stock(1L, "-1")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void anItemCountedInUnitsHasNoDecimals() {
        assertThatThrownBy(() -> bathTowels(List.of(stock(1L, "2.5")), List.of())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void anItemCountedInKilogramsAcceptsDecimals() {
        var detergent = new InventoryItem(1L, "Laundry detergent", "CL-LD-006", "cleaning-supplies", InventoryUnit.KILOGRAMS,
                1L, new BigDecimal("8"), List.of(stock(1L, "12.5")), List.of());
        assertThat(detergent.totalQuantity()).isEqualByComparingTo("12.5");
    }

    @Test
    void rejectsTwoStocksForTheSameLocation() {
        assertThatThrownBy(() -> bathTowels(List.of(stock(1L, "10"), stock(1L, "5")), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void newAdjustmentsAreAddedButRecordedOnesCannotBeRemoved() {
        var opening = adjustment("opening-1-1", StockOperation.IN, "60", 1L);
        var item = bathTowels(List.of(stock(1L, "60")), List.of(opening));

        item.replaceStock(List.of(stock(1L, "50")), List.of(opening, adjustment("adj-2", StockOperation.OUT, "10", 1L)));

        assertThat(item.getAdjustments()).hasSize(2);
        assertThat(item.totalQuantity()).isEqualByComparingTo("50");
        assertThatThrownBy(() -> item.replaceStock(List.of(stock(1L, "50")), List.of(opening)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void usesItsPrimaryLocationAndTheLocationsThatHoldStock() {
        var item = bathTowels(List.of(stock(1L, "0"), stock(3L, "24"), stock(4L, "0")), List.of());
        assertThat(item.usesLocation(1L)).isTrue();
        assertThat(item.usesLocation(3L)).isTrue();
        assertThat(item.usesLocation(4L)).isFalse();
    }
}
