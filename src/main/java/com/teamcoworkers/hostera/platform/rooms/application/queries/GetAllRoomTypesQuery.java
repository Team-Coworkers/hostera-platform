package com.teamcoworkers.hostera.platform.rooms.application.queries;

/** Optional filters are {@code null} when the request does not send them. */
public record GetAllRoomTypesQuery(Long propertyId, String status) {
}
