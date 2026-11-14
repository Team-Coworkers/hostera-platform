package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.entities.InventoryItemPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemPersistenceRepository extends JpaRepository<InventoryItemPersistenceEntity, Long> {
    List<InventoryItemPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByPropertyIdAndCodeIgnoreCase(Long propertyId, String code);
    boolean existsByPropertyIdAndCodeIgnoreCaseAndIdIsNot(Long propertyId, String code, Long id);
}
