package com.teamcoworkers.hostera.platform.rooms.domain.repositories;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link RoomType} aggregates.
 */
public interface RoomTypeRepository {
    Optional<RoomType> findById(Long id);
    List<RoomType> findAll();
    List<RoomType> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByPropertyIdAndName(Long propertyId, String name);
    boolean existsByPropertyIdAndNameAndIdIsNot(Long propertyId, String name, Long id);
    RoomType save(RoomType roomType);
    void deleteById(Long id);
    long count();
}
