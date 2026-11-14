package com.teamcoworkers.hostera.platform.inventory.domain.repositories;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link StorageLocation} aggregates.
 */
public interface StorageLocationRepository {
    Optional<StorageLocation> findById(Long id);
    List<StorageLocation> findAll();
    List<StorageLocation> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByPropertyIdAndCode(Long propertyId, String code);
    boolean existsByPropertyIdAndCodeAndIdIsNot(Long propertyId, String code, Long id);
    StorageLocation save(StorageLocation storageLocation);
    void deleteById(Long id);
    long count();
}
