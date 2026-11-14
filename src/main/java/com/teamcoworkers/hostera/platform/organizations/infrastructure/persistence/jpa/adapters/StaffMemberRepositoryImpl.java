package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.assemblers.StaffMemberPersistenceAssembler;
import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.repositories.StaffMemberPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StaffMemberRepositoryImpl implements StaffMemberRepository {
    private final StaffMemberPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public StaffMemberRepositoryImpl(StaffMemberPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<StaffMember> findById(Long id) {
        return persistenceRepository.findById(id).map(StaffMemberPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<StaffMember> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(StaffMemberPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<StaffMember> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(StaffMemberPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByPropertyId(Long propertyId) {
        return persistenceRepository.existsByPropertyId(propertyId);
    }

    @Override
    public StaffMember save(StaffMember staffMember) {
        var saved = persistenceRepository.save(StaffMemberPersistenceAssembler.toPersistenceFromDomain(staffMember));
        domainEventPublisher.publishAndClearEvents(staffMember);
        return StaffMemberPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public void deleteById(Long id) {
        persistenceRepository.deleteById(id);
    }
}
