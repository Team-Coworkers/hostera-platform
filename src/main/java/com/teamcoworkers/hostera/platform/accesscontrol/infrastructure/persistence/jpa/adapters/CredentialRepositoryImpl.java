package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.assemblers.CredentialPersistenceAssembler;
import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.repositories.CredentialPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CredentialRepositoryImpl implements CredentialRepository {

    private final CredentialPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CredentialRepositoryImpl(CredentialPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Credential> findById(Long id) {
        return persistenceRepository.findById(id).map(CredentialPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Credential> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream()
                .map(CredentialPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Credential> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(CredentialPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Credential> findAllByBookingId(Long bookingId) {
        return persistenceRepository.findAllByBookingIdOrderByIdAsc(bookingId).stream()
                .map(CredentialPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public Credential save(Credential credential) {
        var saved = persistenceRepository.save(CredentialPersistenceAssembler.toPersistenceFromDomain(credential));
        domainEventPublisher.publishAndClearEvents(credential);
        return CredentialPersistenceAssembler.toDomainFromPersistence(saved);
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
