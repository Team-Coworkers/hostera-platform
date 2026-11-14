package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.RoomTypeResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveRoomTypeResource;

public final class RoomTypeResourceFromEntityAssembler {
    private RoomTypeResourceFromEntityAssembler() {
    }

    public static RoomTypeResource toResourceFromEntity(RoomType roomType) {
        return new RoomTypeResource(roomType.getId(),
                roomType.getPropertyId(),
                roomType.getName(),
                roomType.getCapacity(),
                roomType.getBedConfiguration(),
                roomType.getBaseNightlyRate().amount(),
                roomType.getStatus().value());
    }

    public static CreateRoomTypeCommand toCreateCommand(SaveRoomTypeResource resource) {
        return new CreateRoomTypeCommand(resource.propertyId(), resource.name(), resource.capacity(), resource.bedConfiguration(), resource.baseNightlyRate(), resource.status());
    }

    public static UpdateRoomTypeCommand toUpdateCommand(Long roomTypeId, SaveRoomTypeResource resource) {
        return new UpdateRoomTypeCommand(roomTypeId, resource.propertyId(), resource.name(), resource.capacity(), resource.bedConfiguration(), resource.baseNightlyRate(), resource.status());
    }
}
