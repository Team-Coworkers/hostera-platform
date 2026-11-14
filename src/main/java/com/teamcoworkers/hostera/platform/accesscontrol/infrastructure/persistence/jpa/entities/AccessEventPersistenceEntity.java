package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "access_events")
@Getter
@Setter
@NoArgsConstructor
public class AccessEventPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;
    @Column(nullable = false)
    private Instant occurredAt;
    private Long credentialId;
    private String cardId;
    private String holderName;
    private String holderType;
    @Column(nullable = false)
    private String accessPoint;
    private Long roomId;
    @Column(name = "access_result", nullable = false)
    private String result;
    private String denialReason;
}
