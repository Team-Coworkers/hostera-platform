package com.teamcoworkers.hostera.platform.bookings.application.queryservices;

import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllBookingsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetBookingByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingQueryService {
    List<Booking> handle(GetAllBookingsQuery query);

    Optional<Booking> handle(GetBookingByIdQuery query);
}
