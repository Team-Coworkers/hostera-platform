package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers.RatePlanPersistenceAssembler;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories.RatePlanPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RatePlanRepositoryImpl implements RatePlanRepository {
    private final RatePlanPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public RatePlanRepositoryImpl(RatePlanPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<RatePlan> findById(Long id) {
        return persistenceRepository.findById(id).map(RatePlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<RatePlan> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(RatePlanPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<RatePlan> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(RatePlanPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByPropertyIdAndName(Long propertyId, String name) {
        return persistenceRepository.existsByPropertyIdAndNameIgnoreCase(propertyId, name);
    }

    @Override
    public boolean existsByPropertyIdAndNameAndIdIsNot(Long propertyId, String name, Long id) {
        return persistenceRepository.existsByPropertyIdAndNameIgnoreCaseAndIdIsNot(propertyId, name, id);
    }

    @Override
    public RatePlan save(RatePlan ratePlan) {
        var saved = persistenceRepository.save(RatePlanPersistenceAssembler.toPersistenceFromDomain(ratePlan));
        domainEventPublisher.publishAndClearEvents(ratePlan);
        return RatePlanPersistenceAssembler.toDomainFromPersistence(saved);
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
