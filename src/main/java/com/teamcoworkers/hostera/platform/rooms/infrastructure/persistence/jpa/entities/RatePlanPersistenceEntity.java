package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities;

import com.teamcoworkers.hostera.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rate_plans")
@Getter
@Setter
@NoArgsConstructor
public class RatePlanPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false)
    private Long propertyId;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rate_plan_room_types", joinColumns = @JoinColumn(name = "rate_plan_id"))
    @OrderColumn(name = "line_number")
    @Column(name = "room_type_id", nullable = false)
    private List<Long> roomTypeIds = new ArrayList<>();

    @Column(nullable = false, length = 30)
    private String includedServices;

    @Column(nullable = false)
    private boolean refundable;

    @Column(nullable = false, length = 500)
    private String cancellationPolicy;

    @Column(nullable = false, length = 20)
    private String status;
}
