package com.teamcoworkers.hostera.platform.inventory.application.queries;

/** Optional filters are {@code null} when the request does not send them. */
public record GetAllStorageLocationsQuery(Long propertyId, String type) {
}
