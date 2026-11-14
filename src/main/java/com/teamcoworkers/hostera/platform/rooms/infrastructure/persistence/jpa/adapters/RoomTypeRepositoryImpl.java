package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers.RoomTypePersistenceAssembler;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories.RoomTypePersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomTypeRepositoryImpl implements RoomTypeRepository {
    private final RoomTypePersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public RoomTypeRepositoryImpl(RoomTypePersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<RoomType> findById(Long id) {
        return persistenceRepository.findById(id).map(RoomTypePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<RoomType> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(RoomTypePersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<RoomType> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(RoomTypePersistenceAssembler::toDomainFromPersistence).toList();
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
    public RoomType save(RoomType roomType) {
        var saved = persistenceRepository.save(RoomTypePersistenceAssembler.toPersistenceFromDomain(roomType));
        domainEventPublisher.publishAndClearEvents(roomType);
        return RoomTypePersistenceAssembler.toDomainFromPersistence(saved);
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
