package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.assemblers.AccessEventPersistenceAssembler;
import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.repositories.AccessEventPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccessEventRepositoryImpl implements AccessEventRepository {

    private final AccessEventPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public AccessEventRepositoryImpl(AccessEventPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<AccessEvent> findById(Long id) {
        return persistenceRepository.findById(id).map(AccessEventPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<AccessEvent> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream()
                .map(AccessEventPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<AccessEvent> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(AccessEventPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public AccessEvent save(AccessEvent accessEvent) {
        var saved = persistenceRepository.save(AccessEventPersistenceAssembler.toPersistenceFromDomain(accessEvent));
        domainEventPublisher.publishAndClearEvents(accessEvent);
        return AccessEventPersistenceAssembler.toDomainFromPersistence(saved);
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
