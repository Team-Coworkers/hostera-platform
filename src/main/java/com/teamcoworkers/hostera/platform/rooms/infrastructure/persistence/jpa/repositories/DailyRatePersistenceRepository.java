package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.entities.DailyRatePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRatePersistenceRepository extends JpaRepository<DailyRatePersistenceEntity, Long> {
    List<DailyRatePersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByRatePlanIdAndRoomTypeIdAndRateDate(Long ratePlanId, Long roomTypeId, LocalDate rateDate);
    boolean existsByRatePlanIdAndRoomTypeIdAndRateDateAndIdIsNot(Long ratePlanId, Long roomTypeId, LocalDate rateDate, Long id);
    boolean existsByRatePlanId(Long ratePlanId);
}
