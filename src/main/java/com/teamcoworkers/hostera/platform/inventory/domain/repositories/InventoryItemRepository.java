package com.teamcoworkers.hostera.platform.inventory.domain.repositories;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link InventoryItem} aggregates.
 */
public interface InventoryItemRepository {
    Optional<InventoryItem> findById(Long id);
    List<InventoryItem> findAll();
    List<InventoryItem> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByPropertyIdAndCode(Long propertyId, String code);
    boolean existsByPropertyIdAndCodeAndIdIsNot(Long propertyId, String code, Long id);
    InventoryItem save(InventoryItem inventoryItem);
    void deleteById(Long id);
    long count();
}
