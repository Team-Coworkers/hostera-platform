package com.teamcoworkers.hostera.platform.rooms.domain.repositories;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link RatePlan} aggregates.
 */
public interface RatePlanRepository {
    Optional<RatePlan> findById(Long id);
    List<RatePlan> findAll();
    List<RatePlan> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByPropertyIdAndName(Long propertyId, String name);
    boolean existsByPropertyIdAndNameAndIdIsNot(Long propertyId, String name, Long id);
    RatePlan save(RatePlan ratePlan);
    void deleteById(Long id);
    long count();
}
