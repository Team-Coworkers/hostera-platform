package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.RoomTypePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypePersistenceRepository extends JpaRepository<RoomTypePersistenceEntity, Long> {
    List<RoomTypePersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByPropertyIdAndNameIgnoreCase(Long propertyId, String name);
    boolean existsByPropertyIdAndNameIgnoreCaseAndIdIsNot(Long propertyId, String name, Long id);
}
