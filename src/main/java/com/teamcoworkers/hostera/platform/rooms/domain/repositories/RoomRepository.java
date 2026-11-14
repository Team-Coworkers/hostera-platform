package com.teamcoworkers.hostera.platform.rooms.domain.repositories;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link Room} aggregates.
 */
public interface RoomRepository {
    Optional<Room> findById(Long id);
    List<Room> findAll();
    List<Room> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByPropertyIdAndNumber(Long propertyId, String number);
    boolean existsByPropertyIdAndNumberAndIdIsNot(Long propertyId, String number, Long id);
    boolean existsByRoomTypeId(Long roomTypeId);
    Room save(Room room);
    void deleteById(Long id);
    long count();
}
