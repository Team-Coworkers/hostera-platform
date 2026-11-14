package com.teamcoworkers.hostera.platform.rooms.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomTypesQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomTypeByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RoomTypeQueryService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomTypeQueryServiceImpl implements RoomTypeQueryService {
    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeQueryServiceImpl(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    public List<RoomType> handle(GetAllRoomTypesQuery query) {
        var roomTypes = query.propertyId() == null
                ? roomTypeRepository.findAll()
                : roomTypeRepository.findAllByPropertyId(query.propertyId());
        return roomTypes.stream()
                .filter(roomType -> query.status() == null || query.status().equalsIgnoreCase(roomType.getStatus().value()))
                .toList();
    }

    @Override
    public Optional<RoomType> handle(GetRoomTypeByIdQuery query) {
        return roomTypeRepository.findById(query.roomTypeId());
    }
}
