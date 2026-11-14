package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.RatePlanResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveRatePlanResource;

public final class RatePlanResourceFromEntityAssembler {
    private RatePlanResourceFromEntityAssembler() {
    }

    public static RatePlanResource toResourceFromEntity(RatePlan ratePlan) {
        return new RatePlanResource(ratePlan.getId(),
                ratePlan.getPropertyId(),
                ratePlan.getName(),
                ratePlan.getRoomTypeIds(),
                ratePlan.getIncludedServices().value(),
                ratePlan.isRefundable(),
                ratePlan.getCancellationPolicy(),
                ratePlan.getStatus().value());
    }

    public static CreateRatePlanCommand toCreateCommand(SaveRatePlanResource resource) {
        return new CreateRatePlanCommand(resource.propertyId(), resource.name(), resource.roomTypeIds(), resource.includedServices(), resource.refundable(), resource.cancellationPolicy(), resource.status());
    }

    public static UpdateRatePlanCommand toUpdateCommand(Long ratePlanId, SaveRatePlanResource resource) {
        return new UpdateRatePlanCommand(ratePlanId, resource.propertyId(), resource.name(), resource.roomTypeIds(), resource.includedServices(), resource.refundable(), resource.cancellationPolicy(), resource.status());
    }
}
