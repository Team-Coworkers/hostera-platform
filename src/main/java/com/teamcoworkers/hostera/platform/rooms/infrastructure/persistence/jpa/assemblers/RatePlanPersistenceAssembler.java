package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.IncludedServices;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.RatePlanPersistenceEntity;

import java.util.ArrayList;

public final class RatePlanPersistenceAssembler {
    private RatePlanPersistenceAssembler() {
    }

    public static RatePlan toDomainFromPersistence(RatePlanPersistenceEntity entity) {
        if (entity == null) return null;
        return RatePlan.reconstitute(entity.getId(), entity.getPropertyId(), entity.getName(),
                new ArrayList<>(entity.getRoomTypeIds()), IncludedServices.fromValue(entity.getIncludedServices()),
                entity.isRefundable(), entity.getCancellationPolicy(), ActivationStatus.fromValue(entity.getStatus()));
    }

    public static RatePlanPersistenceEntity toPersistenceFromDomain(RatePlan ratePlan) {
        var entity = new RatePlanPersistenceEntity();
        if (ratePlan.getId() != null) entity.setId(ratePlan.getId());
        entity.setPropertyId(ratePlan.getPropertyId());
        entity.setName(ratePlan.getName());
        entity.setRoomTypeIds(new ArrayList<>(ratePlan.getRoomTypeIds()));
        entity.setIncludedServices(ratePlan.getIncludedServices().value());
        entity.setRefundable(ratePlan.isRefundable());
        entity.setCancellationPolicy(ratePlan.getCancellationPolicy());
        entity.setStatus(ratePlan.getStatus().value());
        return entity;
    }
}
