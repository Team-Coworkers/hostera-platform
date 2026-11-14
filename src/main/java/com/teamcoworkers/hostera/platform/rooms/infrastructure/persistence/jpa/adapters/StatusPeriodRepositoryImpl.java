package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.StatusPeriodRepository;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers.StatusPeriodPersistenceAssembler;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories.StatusPeriodPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StatusPeriodRepositoryImpl implements StatusPeriodRepository {
    private final StatusPeriodPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public StatusPeriodRepositoryImpl(StatusPeriodPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<StatusPeriod> findById(Long id) {
        return persistenceRepository.findById(id).map(StatusPeriodPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<StatusPeriod> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(StatusPeriodPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<StatusPeriod> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(StatusPeriodPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public List<StatusPeriod> findAllByRoomId(Long roomId) {
        return persistenceRepository.findAllByRoomIdOrderByIdAsc(roomId).stream()
                .map(StatusPeriodPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public StatusPeriod save(StatusPeriod statusPeriod) {
        var saved = persistenceRepository.save(StatusPeriodPersistenceAssembler.toPersistenceFromDomain(statusPeriod));
        domainEventPublisher.publishAndClearEvents(statusPeriod);
        return StatusPeriodPersistenceAssembler.toDomainFromPersistence(saved);
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
