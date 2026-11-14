package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
public class PropertyPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 3)
    private String currency;
}
