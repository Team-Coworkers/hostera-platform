package com.teamcoworkers.hostera.platform.rooms.domain.repositories;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link StatusPeriod} aggregates.
 */
public interface StatusPeriodRepository {
    Optional<StatusPeriod> findById(Long id);
    List<StatusPeriod> findAll();
    List<StatusPeriod> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    List<StatusPeriod> findAllByRoomId(Long roomId);
    StatusPeriod save(StatusPeriod statusPeriod);
    void deleteById(Long id);
    long count();
}
