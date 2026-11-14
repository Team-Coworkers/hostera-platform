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
import java.time.LocalDate;

/**
 * Row of the bookings table. The moment the booking was made is stored as {@code booked_at}
 * because {@code created_at} is the audit column every entity inherits.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class BookingPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String preferredLanguage;
    private String guestDocumentType;
    private String guestDocumentNumber;
    @Column(nullable = false)
    private LocalDate checkInDate;
    @Column(nullable = false)
    private LocalDate checkOutDate;
    @Column(nullable = false)
    private Integer guests;
    private Long roomTypeId;
    private Long roomId;
    private Long ratePlanId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;
    @Column(length = 1000)
    private String guestRequest;
    @Column(name = "booked_at", nullable = false)
    private LocalDate bookedAt;
    private Instant confirmedAt;
    private String confirmedBy;
    private Instant cancelledAt;
    private String cancelledBy;
    private String cancellationReason;
    @Column(length = 1000)
    private String cancellationNote;
    private Instant noShowAt;
    private String noShowBy;
    private Instant checkedInAt;
    private String checkedInBy;
    private Instant checkedOutAt;
    private String checkedOutBy;
    private String roomCondition;
    @Column(length = 1000)
    private String departureNote;
}
