package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.StatusPeriodResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveStatusPeriodResource;

public final class StatusPeriodResourceFromEntityAssembler {
    private StatusPeriodResourceFromEntityAssembler() {
    }

    public static StatusPeriodResource toResourceFromEntity(StatusPeriod statusPeriod) {
        return new StatusPeriodResource(statusPeriod.getId(),
                statusPeriod.getPropertyId(),
                statusPeriod.getRoomId(),
                statusPeriod.getStatus().value(),
                statusPeriod.getPeriod().startDate().toString(),
                statusPeriod.getPeriod().endDate().toString(),
                statusPeriod.getReason());
    }

    public static CreateStatusPeriodCommand toCreateCommand(SaveStatusPeriodResource resource) {
        return new CreateStatusPeriodCommand(resource.propertyId(), resource.roomId(), resource.status(), resource.startDate(), resource.endDate(), resource.reason());
    }

    public static UpdateStatusPeriodCommand toUpdateCommand(Long statusPeriodId, SaveStatusPeriodResource resource) {
        return new UpdateStatusPeriodCommand(statusPeriodId, resource.propertyId(), resource.roomId(), resource.status(), resource.startDate(), resource.endDate(), resource.reason());
    }
}
