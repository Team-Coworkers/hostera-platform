package com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects;

/**
 * Direction of a stock adjustment: stock that comes in or stock that goes out.
 */
public enum StockOperation {
    IN("in"),
    OUT("out");

    private final String value;

    StockOperation(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    public static StockOperation fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Stock operation must be one of: in, out");
    }
}
