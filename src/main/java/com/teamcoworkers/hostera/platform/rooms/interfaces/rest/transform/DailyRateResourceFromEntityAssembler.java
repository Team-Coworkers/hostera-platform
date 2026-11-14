package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.DailyRateResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveDailyRateResource;

public final class DailyRateResourceFromEntityAssembler {
    private DailyRateResourceFromEntityAssembler() {
    }

    public static DailyRateResource toResourceFromEntity(DailyRate dailyRate) {
        return new DailyRateResource(dailyRate.getId(),
                dailyRate.getPropertyId(),
                dailyRate.getRatePlanId(),
                dailyRate.getRoomTypeId(),
                dailyRate.getDate().toString(),
                dailyRate.getAmount().amount());
    }

    public static CreateDailyRateCommand toCreateCommand(SaveDailyRateResource resource) {
        return new CreateDailyRateCommand(resource.propertyId(), resource.ratePlanId(), resource.roomTypeId(), resource.date(), resource.amount());
    }

    public static UpdateDailyRateCommand toUpdateCommand(Long dailyRateId, SaveDailyRateResource resource) {
        return new UpdateDailyRateCommand(dailyRateId, resource.propertyId(), resource.ratePlanId(), resource.roomTypeId(), resource.date(), resource.amount());
    }
}
