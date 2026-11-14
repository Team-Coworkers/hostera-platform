package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentPersistenceRepository extends JpaRepository<PaymentPersistenceEntity, Long> {
    List<PaymentPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);

    List<PaymentPersistenceEntity> findAllByBookingIdOrderByIdAsc(Long bookingId);
}
