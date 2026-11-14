package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.DailyRatePersistenceEntity;

public final class DailyRatePersistenceAssembler {
    private DailyRatePersistenceAssembler() {
    }

    public static DailyRate toDomainFromPersistence(DailyRatePersistenceEntity entity) {
        if (entity == null) return null;
        return DailyRate.reconstitute(entity.getId(), entity.getPropertyId(), entity.getRatePlanId(), entity.getRoomTypeId(),
                entity.getRateDate(), new NightlyRate(entity.getAmount()));
    }

    public static DailyRatePersistenceEntity toPersistenceFromDomain(DailyRate dailyRate) {
        var entity = new DailyRatePersistenceEntity();
        if (dailyRate.getId() != null) entity.setId(dailyRate.getId());
        entity.setPropertyId(dailyRate.getPropertyId());
        entity.setRatePlanId(dailyRate.getRatePlanId());
        entity.setRoomTypeId(dailyRate.getRoomTypeId());
        entity.setRateDate(dailyRate.getDate());
        entity.setAmount(dailyRate.getAmount().amount());
        return entity;
    }
}
