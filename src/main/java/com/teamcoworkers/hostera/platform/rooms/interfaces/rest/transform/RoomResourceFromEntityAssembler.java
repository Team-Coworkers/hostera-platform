package com.teamcoworkers.hostera.platform.rooms.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.RoomResource;
import com.teamcoworkers.hostera.platform.rooms.interfaces.rest.resources.SaveRoomResource;

public final class RoomResourceFromEntityAssembler {
    private RoomResourceFromEntityAssembler() {
    }

    public static RoomResource toResourceFromEntity(Room room) {
        return new RoomResource(room.getId(),
                room.getPropertyId(),
                room.getNumber(),
                room.getRoomTypeId(),
                room.getFloor(),
                room.getStatus().value());
    }

    public static CreateRoomCommand toCreateCommand(SaveRoomResource resource) {
        return new CreateRoomCommand(resource.propertyId(), resource.number(), resource.roomTypeId(), resource.floor(), resource.status());
    }

    public static UpdateRoomCommand toUpdateCommand(Long roomId, SaveRoomResource resource) {
        return new UpdateRoomCommand(roomId, resource.propertyId(), resource.number(), resource.roomTypeId(), resource.floor(), resource.status());
    }
}
