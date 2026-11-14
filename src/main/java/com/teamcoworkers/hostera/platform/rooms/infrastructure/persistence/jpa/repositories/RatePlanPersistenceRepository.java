package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.RatePlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatePlanPersistenceRepository extends JpaRepository<RatePlanPersistenceEntity, Long> {
    List<RatePlanPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByPropertyIdAndNameIgnoreCase(Long propertyId, String name);
    boolean existsByPropertyIdAndNameIgnoreCaseAndIdIsNot(Long propertyId, String name, Long id);
}
