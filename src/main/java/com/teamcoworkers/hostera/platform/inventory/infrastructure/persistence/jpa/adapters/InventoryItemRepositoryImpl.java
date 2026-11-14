package com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.InventoryItemRepository;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.assemblers.InventoryItemPersistenceAssembler;
import com.teamcoworkers.hostera.platform.inventory.infrastructure.persistence.jpa.repositories.InventoryItemPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InventoryItemRepositoryImpl implements InventoryItemRepository {
    private final InventoryItemPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public InventoryItemRepositoryImpl(InventoryItemPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<InventoryItem> findById(Long id) {
        return persistenceRepository.findById(id).map(InventoryItemPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<InventoryItem> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(InventoryItemPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<InventoryItem> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(InventoryItemPersistenceAssembler::toDomainFromPersistence).toList();
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
    public InventoryItem save(InventoryItem inventoryItem) {
        var saved = persistenceRepository.save(InventoryItemPersistenceAssembler.toPersistenceFromDomain(inventoryItem));
        domainEventPublisher.publishAndClearEvents(inventoryItem);
        return InventoryItemPersistenceAssembler.toDomainFromPersistence(saved);
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
