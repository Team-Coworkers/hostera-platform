package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.RoomPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomPersistenceRepository extends JpaRepository<RoomPersistenceEntity, Long> {
    List<RoomPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByPropertyIdAndRoomNumberIgnoreCase(Long propertyId, String roomNumber);
    boolean existsByPropertyIdAndRoomNumberIgnoreCaseAndIdIsNot(Long propertyId, String roomNumber, Long id);
    boolean existsByRoomTypeId(Long roomTypeId);
}
