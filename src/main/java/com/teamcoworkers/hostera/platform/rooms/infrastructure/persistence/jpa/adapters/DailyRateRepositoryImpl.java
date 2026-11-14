package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.DailyRateRepository;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers.DailyRatePersistenceAssembler;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories.DailyRatePersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class DailyRateRepositoryImpl implements DailyRateRepository {
    private final DailyRatePersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public DailyRateRepositoryImpl(DailyRatePersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<DailyRate> findById(Long id) {
        return persistenceRepository.findById(id).map(DailyRatePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<DailyRate> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(DailyRatePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<DailyRate> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(DailyRatePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByRatePlanIdAndRoomTypeIdAndDate(Long ratePlanId, Long roomTypeId, LocalDate date) {
        return persistenceRepository.existsByRatePlanIdAndRoomTypeIdAndRateDate(ratePlanId, roomTypeId, date);
    }

    @Override
    public boolean existsByRatePlanIdAndRoomTypeIdAndDateAndIdIsNot(Long ratePlanId, Long roomTypeId, LocalDate date, Long id) {
        return persistenceRepository.existsByRatePlanIdAndRoomTypeIdAndRateDateAndIdIsNot(ratePlanId, roomTypeId, date, id);
    }

    @Override
    public boolean existsByRatePlanId(Long ratePlanId) {
        return persistenceRepository.existsByRatePlanId(ratePlanId);
    }

    @Override
    public DailyRate save(DailyRate dailyRate) {
        var saved = persistenceRepository.save(DailyRatePersistenceAssembler.toPersistenceFromDomain(dailyRate));
        domainEventPublisher.publishAndClearEvents(dailyRate);
        return DailyRatePersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        persistenceRepository.deleteById(id);
    }

    @Override
    public long count() {
        return persistenceRepository.count();
    }
}
