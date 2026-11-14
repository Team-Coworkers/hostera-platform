package com.teamcoworkers.hostera.platform.bookings.domain.repositories;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(Long id);

    List<Payment> findAll();

    List<Payment> findAllByPropertyId(Long propertyId);

    List<Payment> findAllByBookingId(Long bookingId);

    boolean existsById(Long id);

    Payment save(Payment payment);

    void deleteById(Long id);

    long count();
}
