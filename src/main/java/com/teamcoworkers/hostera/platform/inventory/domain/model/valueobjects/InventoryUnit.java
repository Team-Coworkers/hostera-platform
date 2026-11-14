package com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects;

/**
 * Unit in which the stock of an inventory item is counted.
 */
public enum InventoryUnit {
    UNITS("units"),
    LITERS("liters"),
    KILOGRAMS("kilograms");

    private final String value;

    InventoryUnit(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    /** @return whether the quantities in this unit cannot have decimals */
    public boolean requiresWholeQuantities() {
        return this == UNITS;
    }

    public static InventoryUnit fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Unit must be one of: units, liters, kilograms");
    }
}
