package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.assemblers.PropertyPersistenceAssembler;
import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.repositories.PropertyPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PropertyRepositoryImpl implements PropertyRepository {
    private final PropertyPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PropertyRepositoryImpl(PropertyPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Property> findById(Long id) {
        return persistenceRepository.findById(id).map(PropertyPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Property> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(PropertyPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Property> findAllByOrganizationId(Long organizationId) {
        return persistenceRepository.findAllByOrganizationIdOrderByIdAsc(organizationId).stream()
                .map(PropertyPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByOrganizationIdAndName(Long organizationId, String name) {
        return persistenceRepository.existsByOrganizationIdAndNameIgnoreCase(organizationId, name);
    }

    @Override
    public boolean existsByOrganizationIdAndNameAndIdIsNot(Long organizationId, String name, Long id) {
        return persistenceRepository.existsByOrganizationIdAndNameIgnoreCaseAndIdIsNot(organizationId, name, id);
    }

    @Override
    public Property save(Property property) {
        var saved = persistenceRepository.save(PropertyPersistenceAssembler.toPersistenceFromDomain(property));
        domainEventPublisher.publishAndClearEvents(property);
        return PropertyPersistenceAssembler.toDomainFromPersistence(saved);
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
