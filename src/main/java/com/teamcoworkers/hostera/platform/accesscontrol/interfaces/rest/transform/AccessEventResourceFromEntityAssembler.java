package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.AccessEventData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.UpdateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.AccessEventResource;
import com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest.resources.SaveAccessEventResource;

public final class AccessEventResourceFromEntityAssembler {

    private AccessEventResourceFromEntityAssembler() {
    }

    public static AccessEventResource toResourceFromEntity(AccessEvent event) {
        return new AccessEventResource(event.getId(), event.getPropertyId(), event.getOccurredAt(),
                event.getCredentialId(), event.getCardId(), event.getHolderName(),
                event.getHolderType() == null ? null : event.getHolderType().value(), event.getAccessPoint().value(),
                event.getRoomId(), event.getResult().value(),
                event.getDenialReason() == null ? null : event.getDenialReason().value());
    }

    public static CreateAccessEventCommand toCreateCommand(SaveAccessEventResource resource) {
        return new CreateAccessEventCommand(toData(resource));
    }

    public static UpdateAccessEventCommand toUpdateCommand(Long accessEventId, SaveAccessEventResource resource) {
        return new UpdateAccessEventCommand(accessEventId, toData(resource));
    }

    private static AccessEventData toData(SaveAccessEventResource resource) {
        return new AccessEventData(resource.propertyId(), resource.occurredAt(), resource.credentialId(),
                resource.accessPoint(), resource.roomId());
    }
}
