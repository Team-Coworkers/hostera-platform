package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.PaymentMethod;
import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;

public final class PaymentPersistenceAssembler {

    private PaymentPersistenceAssembler() {
    }

    public static Payment toDomainFromPersistence(PaymentPersistenceEntity entity) {
        if (entity == null) return null;
        return Payment.reconstitute(entity.getId(), entity.getPropertyId(), entity.getBookingId(), entity.getAmount(),
                PaymentMethod.fromValue(entity.getMethod()), entity.getPaidAt(), entity.getReference(),
                entity.getRecordedBy());
    }

    public static PaymentPersistenceEntity toPersistenceFromDomain(Payment payment) {
        var entity = new PaymentPersistenceEntity();
        if (payment.getId() != null) entity.setId(payment.getId());
        entity.setPropertyId(payment.getPropertyId());
        entity.setBookingId(payment.getBookingId());
        entity.setAmount(payment.getAmount());
        entity.setMethod(payment.getMethod().value());
        entity.setPaidAt(payment.getPaidAt());
        entity.setReference(payment.getReference());
        entity.setRecordedBy(payment.getRecordedBy());
        return entity;
    }
}
