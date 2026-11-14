package com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.PaymentMethod;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Payment aggregate root: money collected for one booking at its property.
 * <p>
 * Invariants: it belongs to a property and a booking, its amount is greater than zero and it
 * records how and when it was paid.
 * </p>
 */
@Getter
public class Payment extends AbstractDomainAggregateRoot<Payment> {

    private Long id;
    private Long propertyId;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentMethod method;
    private Instant paidAt;
    private String reference;
    private String recordedBy;

    public Payment(Long propertyId, Long bookingId, BigDecimal amount, PaymentMethod method, Instant paidAt,
                   String reference, String recordedBy) {
        updateInformation(propertyId, bookingId, amount, method, paidAt, reference, recordedBy);
    }

    public static Payment reconstitute(Long id, Long propertyId, Long bookingId, BigDecimal amount,
                                       PaymentMethod method, Instant paidAt, String reference, String recordedBy) {
        var payment = new Payment(propertyId, bookingId, amount, method, paidAt, reference, recordedBy);
        payment.id = id;
        return payment;
    }

    public Payment updateInformation(Long propertyId, Long bookingId, BigDecimal amount, PaymentMethod method,
                                     Instant paidAt, String reference, String recordedBy) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (bookingId == null || bookingId <= 0)
            throw new IllegalArgumentException("Booking id must be a positive number");
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        if (method == null) throw new IllegalArgumentException("Payment method is required");
        this.propertyId = propertyId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.paidAt = paidAt == null ? Instant.now() : paidAt;
        this.reference = reference == null ? "" : reference.trim();
        this.recordedBy = recordedBy == null ? "" : recordedBy.trim();
        return this;
    }
}
