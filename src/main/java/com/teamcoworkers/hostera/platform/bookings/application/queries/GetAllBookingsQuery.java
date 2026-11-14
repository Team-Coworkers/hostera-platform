package com.teamcoworkers.hostera.platform.bookings.application.queries;

/**
 * Bookings filtered by equality, as the frontend asks for them. {@code sort}, {@code order} and
 * {@code limit} mirror the JSON Server parameters {@code _sort}, {@code _order} and {@code _limit}
 * that the frontend uses to find the booking with the highest code.
 */
public record GetAllBookingsQuery(Long propertyId, String status, Long roomId, String sort, String order,
                                  Integer limit) {
    public GetAllBookingsQuery(Long propertyId) {
        this(propertyId, null, null, null, null, null);
    }
}
