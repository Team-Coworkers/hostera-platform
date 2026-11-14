package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.RoomPersistenceEntity;

public final class RoomPersistenceAssembler {
    private RoomPersistenceAssembler() {
    }

    public static Room toDomainFromPersistence(RoomPersistenceEntity entity) {
        if (entity == null) return null;
        return Room.reconstitute(entity.getId(), entity.getPropertyId(), entity.getRoomNumber(), entity.getRoomTypeId(),
                entity.getFloor(), RoomStatus.fromValue(entity.getStatus()));
    }

    public static RoomPersistenceEntity toPersistenceFromDomain(Room room) {
        var entity = new RoomPersistenceEntity();
        if (room.getId() != null) entity.setId(room.getId());
        entity.setPropertyId(room.getPropertyId());
        entity.setRoomNumber(room.getNumber());
        entity.setRoomTypeId(room.getRoomTypeId());
        entity.setFloor(room.getFloor());
        entity.setStatus(room.getStatus().value());
        return entity;
    }
}
