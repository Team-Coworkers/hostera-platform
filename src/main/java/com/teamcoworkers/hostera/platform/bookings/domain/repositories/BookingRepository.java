package com.teamcoworkers.hostera.platform.bookings.domain.repositories;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    List<Booking> findAllByPropertyId(Long propertyId);

    List<Booking> findAllByRoomId(Long roomId);

    boolean existsById(Long id);

    Booking save(Booking booking);

    void deleteById(Long id);

    long count();
}
