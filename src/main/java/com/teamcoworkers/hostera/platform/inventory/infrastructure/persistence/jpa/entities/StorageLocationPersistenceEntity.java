package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "storage_locations")
@Getter
@Setter
@NoArgsConstructor
public class StorageLocationPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String responsibleTeam;

    @Column(nullable = false, length = 500)
    private String description;
}
