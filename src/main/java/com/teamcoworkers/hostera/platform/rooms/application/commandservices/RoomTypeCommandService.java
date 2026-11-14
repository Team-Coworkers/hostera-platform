package com.teamcoworkers.hostera.platform.rooms.application.commandservices;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface RoomTypeCommandService {
    Result<RoomType, ApplicationError> handle(CreateRoomTypeCommand command);
    Result<RoomType, ApplicationError> handle(UpdateRoomTypeCommand command);
    Result<Long, ApplicationError> handle(DeleteRoomTypeCommand command);
}
