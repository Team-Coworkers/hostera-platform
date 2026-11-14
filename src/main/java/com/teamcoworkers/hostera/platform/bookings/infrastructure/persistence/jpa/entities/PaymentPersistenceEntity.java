package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class PaymentPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;
    @Column(nullable = false)
    private Long bookingId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private String method;
    @Column(nullable = false)
    private Instant paidAt;
    private String reference;
    private String recordedBy;
}
