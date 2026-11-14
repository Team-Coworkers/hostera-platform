package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
public class RoomTypePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String bedConfiguration;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal baseNightlyRate;

    @Column(nullable = false, length = 20)
    private String status;
}
