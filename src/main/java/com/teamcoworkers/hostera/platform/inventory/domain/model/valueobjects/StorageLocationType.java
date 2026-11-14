package com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects;

/**
 * Kind of place where a property keeps its supplies.
 */
public enum StorageLocationType {
    WAREHOUSE("warehouse"),
    CLOSET("closet"),
    FRONT_DESK("front-desk"),
    SERVICE_AREA("service-area");

    private final String value;

    StorageLocationType(String value) {
        this.value = value;
    }

    /** @return the value exchanged with the clients of the API */
    public String value() {
        return value;
    }

    public static StorageLocationType fromValue(String value) {
        if (value != null)
            for (var candidate : values())
                if (candidate.value.equalsIgnoreCase(value.trim())) return candidate;
        throw new IllegalArgumentException("Storage location type must be one of: warehouse, closet, front-desk, service-area");
    }
}
