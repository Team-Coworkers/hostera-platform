package com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects;

/**
 * Condition of the total stock of an inventory item compared with its low-stock threshold.
 */
public enum StockCondition {
    IN_STOCK("in-stock"),
    LOW_STOCK("low-stock"),
    OUT_OF_STOCK("out-of-stock");

    private final String value;

    StockCondition(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }
}
