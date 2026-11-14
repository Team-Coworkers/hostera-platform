package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities.StorageLocationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageLocationPersistenceRepository extends JpaRepository<StorageLocationPersistenceEntity, Long> {
    List<StorageLocationPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByPropertyIdAndCodeIgnoreCase(Long propertyId, String code);
    boolean existsByPropertyIdAndCodeIgnoreCaseAndIdIsNot(Long propertyId, String code, Long id);
}
