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
@Table(name = "credentials")
@Getter
@Setter
@NoArgsConstructor
public class CredentialPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;
    @Column(nullable = false)
    private String cardId;
    @Column(name = "credential_type", nullable = false)
    private String type;
    @Column(nullable = false)
    private String holderName;
    private Long staffMemberId;
    private Long bookingId;
    private String bookingCode;
    private Long roomId;
    @Column(name = "access_scope")
    private String scope;
    @Column(nullable = false)
    private Instant validFrom;
    private Instant validUntil;
    @Column(nullable = false)
    private Instant issuedAt;
    private String issuedBy;
    private Instant revokedAt;
    private String revokedBy;
    private String revocationReason;
    @Column(length = 1000)
    private String revocationNote;
}
