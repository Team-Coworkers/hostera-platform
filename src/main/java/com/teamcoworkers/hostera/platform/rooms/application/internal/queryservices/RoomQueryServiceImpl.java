package com.teamcoworkers.hostera.platform.rooms.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RoomQueryService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomQueryServiceImpl implements RoomQueryService {
    private final RoomRepository roomRepository;

    public RoomQueryServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> handle(GetAllRoomsQuery query) {
        var rooms = query.propertyId() == null
                ? roomRepository.findAll()
                : roomRepository.findAllByPropertyId(query.propertyId());
        return rooms.stream()
                .filter(room -> query.roomTypeId() == null || query.roomTypeId().equals(room.getRoomTypeId()))
                .filter(room -> query.status() == null || query.status().equalsIgnoreCase(room.getStatus().value()))
                .toList();
    }

    @Override
    public Optional<Room> handle(GetRoomByIdQuery query) {
        return roomRepository.findById(query.roomId());
    }
}
