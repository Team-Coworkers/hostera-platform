package com.teamcoworkers.hostera.platform.rooms.domain.repositories;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link DailyRate} aggregates.
 */
public interface DailyRateRepository {
    Optional<DailyRate> findById(Long id);
    List<DailyRate> findAll();
    List<DailyRate> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByRatePlanIdAndRoomTypeIdAndDate(Long ratePlanId, Long roomTypeId, LocalDate date);
    boolean existsByRatePlanIdAndRoomTypeIdAndDateAndIdIsNot(Long ratePlanId, Long roomTypeId, LocalDate date, Long id);
    boolean existsByRatePlanId(Long ratePlanId);
    DailyRate save(DailyRate dailyRate);
    void deleteById(Long id);
    long count();
}
