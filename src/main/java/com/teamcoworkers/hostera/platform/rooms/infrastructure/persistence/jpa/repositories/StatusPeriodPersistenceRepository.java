package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.StatusPeriodPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusPeriodPersistenceRepository extends JpaRepository<StatusPeriodPersistenceEntity, Long> {
    List<StatusPeriodPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    List<StatusPeriodPersistenceEntity> findAllByRoomIdOrderByIdAsc(Long roomId);
}
