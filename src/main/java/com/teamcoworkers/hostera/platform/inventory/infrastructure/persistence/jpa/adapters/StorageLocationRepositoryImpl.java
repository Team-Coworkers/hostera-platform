package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.StorageLocationRepository;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.assemblers.StorageLocationPersistenceAssembler;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.repositories.StorageLocationPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StorageLocationRepositoryImpl implements StorageLocationRepository {
    private final StorageLocationPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public StorageLocationRepositoryImpl(StorageLocationPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<StorageLocation> findById(Long id) {
        return persistenceRepository.findById(id).map(StorageLocationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<StorageLocation> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(StorageLocationPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<StorageLocation> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(StorageLocationPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByPropertyIdAndCode(Long propertyId, String code) {
        return persistenceRepository.existsByPropertyIdAndCodeIgnoreCase(propertyId, code);
    }

    @Override
    public boolean existsByPropertyIdAndCodeAndIdIsNot(Long propertyId, String code, Long id) {
        return persistenceRepository.existsByPropertyIdAndCodeIgnoreCaseAndIdIsNot(propertyId, code, id);
    }

    @Override
    public StorageLocation save(StorageLocation storageLocation) {
        var saved = persistenceRepository.save(StorageLocationPersistenceAssembler.toPersistenceFromDomain(storageLocation));
        domainEventPublisher.publishAndClearEvents(storageLocation);
        return StorageLocationPersistenceAssembler.toDomainFromPersistence(saved);
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
