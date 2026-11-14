package com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockCondition;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Inventory item aggregate root: a supply of a property (linen, amenities, cleaning or access
 * supplies) with its stock per storage location and the history of its stock adjustments. The
 * stocks and the adjustments are saved together with the item in one transaction.
 * <p>
 * Invariants: it belongs to a property; it has a name, a code (unique in the property, which the
 * application layer checks), a category, a unit and a primary storage location; its low-stock
 * threshold and its stocks are never negative; it has one stock per storage location; an item
 * counted in units has no decimals; and the recorded stock adjustments are never removed.
 * </p>
 */
@Getter
public class InventoryItem extends AbstractDomainAggregateRoot<InventoryItem> {
    private Long id;
    private Long propertyId;
    private String name;
    private String code;
    private String category;
    private InventoryUnit unit;
    private Long primaryLocationId;
    private BigDecimal lowStockThreshold;
    private List<Stock> stocks;
    private List<StockAdjustment> adjustments;

    public InventoryItem(Long propertyId, String name, String code, String category, InventoryUnit unit,
                         Long primaryLocationId, BigDecimal lowStockThreshold, List<Stock> stocks,
                         List<StockAdjustment> adjustments) {
        updateInformation(propertyId, name, code, category, unit, primaryLocationId, lowStockThreshold);
        replaceStock(stocks, adjustments);
    }

    public static InventoryItem reconstitute(Long id, Long propertyId, String name, String code, String category,
                                             InventoryUnit unit, Long primaryLocationId, BigDecimal lowStockThreshold,
                                             List<Stock> stocks, List<StockAdjustment> adjustments) {
        var inventoryItem = new InventoryItem(propertyId, name, code, category, unit, primaryLocationId, lowStockThreshold,
                stocks, adjustments);
        inventoryItem.id = id;
        return inventoryItem;
    }

    public InventoryItem updateInformation(Long propertyId, String name, String code, String category, InventoryUnit unit,
                                           Long primaryLocationId, BigDecimal lowStockThreshold) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Inventory item name is required");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Inventory item code is required");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Inventory item category is required");
        if (unit == null) throw new IllegalArgumentException("Inventory item unit is required");
        if (primaryLocationId == null || primaryLocationId <= 0)
            throw new IllegalArgumentException("Primary location id must be a positive number");
        if (lowStockThreshold == null || lowStockThreshold.signum() < 0)
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        if (unit.requiresWholeQuantities() && hasDecimals(lowStockThreshold))
            throw new IllegalArgumentException("An item counted in units has a whole low stock threshold");
        this.propertyId = propertyId;
        this.name = name.trim();
        this.code = code.trim();
        this.category = category.trim();
        this.unit = unit;
        this.primaryLocationId = primaryLocationId;
        this.lowStockThreshold = lowStockThreshold;
        return this;
    }

    /**
     * Replaces the stock per location and the adjustment history of the item.
     *
     * @throws IllegalStateException when an adjustment that was already recorded is missing
     */
    public InventoryItem replaceStock(List<Stock> stocks, List<StockAdjustment> adjustments) {
        List<Stock> newStocks = stocks == null ? List.of() : List.copyOf(stocks);
        List<StockAdjustment> newAdjustments = adjustments == null ? List.of() : List.copyOf(adjustments);
        if (newStocks.stream().map(Stock::locationId).distinct().count() != newStocks.size())
            throw new IllegalArgumentException("An item has only one stock per storage location");
        if (newAdjustments.stream().map(StockAdjustment::id).distinct().count() != newAdjustments.size())
            throw new IllegalArgumentException("Stock adjustment ids must be unique in the item");
        if (unit.requiresWholeQuantities()) {
            if (newStocks.stream().anyMatch(stock -> hasDecimals(stock.quantity()))
                    || newAdjustments.stream().anyMatch(adjustment -> hasDecimals(adjustment.quantity())))
                throw new IllegalArgumentException("An item counted in units has whole quantities");
        }
        if (this.adjustments != null) {
            var newIds = newAdjustments.stream().map(StockAdjustment::id).toList();
            if (!this.adjustments.stream().map(StockAdjustment::id).allMatch(newIds::contains))
                throw new IllegalStateException("Recorded stock adjustments cannot be removed");
        }
        this.stocks = newStocks;
        this.adjustments = newAdjustments;
        return this;
    }

    /** @return the stock of the item in all its storage locations */
    public BigDecimal totalQuantity() {
        return stocks.stream().map(Stock::quantity).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** @return the stock of the item at one storage location, zero when it has none there */
    public BigDecimal quantityAt(Long locationId) {
        return stocks.stream().filter(stock -> stock.locationId().equals(locationId))
                .map(Stock::quantity).findFirst().orElse(BigDecimal.ZERO);
    }

    public StockCondition stockCondition() {
        var total = totalQuantity();
        if (total.signum() == 0) return StockCondition.OUT_OF_STOCK;
        return total.compareTo(lowStockThreshold) <= 0 ? StockCondition.LOW_STOCK : StockCondition.IN_STOCK;
    }

    /** @return whether the location is the primary one of the item or still holds stock of it */
    public boolean usesLocation(Long locationId) {
        return primaryLocationId.equals(locationId) || quantityAt(locationId).signum() > 0;
    }

    private static boolean hasDecimals(BigDecimal quantity) {
        return quantity.stripTrailingZeros().scale() > 0;
    }
}
