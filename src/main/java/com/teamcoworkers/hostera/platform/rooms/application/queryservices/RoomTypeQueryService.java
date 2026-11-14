package com.teamcoworkers.hostera.platform.rooms.application.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomTypesQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomTypeByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomTypeQueryService {
    List<RoomType> handle(GetAllRoomTypesQuery query);
    Optional<RoomType> handle(GetRoomTypeByIdQuery query);
}
