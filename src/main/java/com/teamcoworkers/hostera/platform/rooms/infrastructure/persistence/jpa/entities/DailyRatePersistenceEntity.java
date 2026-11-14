package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_rates")
@Getter
@Setter
@NoArgsConstructor
public class DailyRatePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private Long ratePlanId;

    @Column(nullable = false)
    private Long roomTypeId;

    @Column(nullable = false)
    private LocalDate rateDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
}
