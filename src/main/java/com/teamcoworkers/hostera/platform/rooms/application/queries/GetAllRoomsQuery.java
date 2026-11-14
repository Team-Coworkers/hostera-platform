package com.teamcoworkers.hostera.platform.rooms.application.queries;

/** Optional filters are {@code null} when the request does not send them. */
public record GetAllRoomsQuery(Long propertyId, Long roomTypeId, String status) {
}
