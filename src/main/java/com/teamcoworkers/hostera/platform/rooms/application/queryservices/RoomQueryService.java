package com.teamcoworkers.hostera.platform.rooms.application.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;

import java.util.List;
import java.util.Optional;

public interface RoomQueryService {
    List<Room> handle(GetAllRoomsQuery query);
    Optional<Room> handle(GetRoomByIdQuery query);
}
