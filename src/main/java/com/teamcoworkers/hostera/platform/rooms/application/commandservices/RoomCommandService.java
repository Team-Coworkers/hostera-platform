package com.teamcoworkers.hostera.platform.rooms.application.commandservices;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface RoomCommandService {
    Result<Room, ApplicationError> handle(CreateRoomCommand command);
    Result<Room, ApplicationError> handle(UpdateRoomCommand command);
    Result<Long, ApplicationError> handle(DeleteRoomCommand command);
}
