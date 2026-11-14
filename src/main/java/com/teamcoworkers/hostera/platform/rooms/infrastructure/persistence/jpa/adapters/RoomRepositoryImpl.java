package com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.assemblers.RoomPersistenceAssembler;
import com.teamcoworkers.hostera.platform.rooms.infrastructure.persistence.jpa.repositories.RoomPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public RoomRepositoryImpl(RoomPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return persistenceRepository.findById(id).map(RoomPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Room> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream().map(RoomPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Room> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(RoomPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByPropertyIdAndNumber(Long propertyId, String number) {
        return persistenceRepository.existsByPropertyIdAndRoomNumberIgnoreCase(propertyId, number);
    }

    @Override
    public boolean existsByPropertyIdAndNumberAndIdIsNot(Long propertyId, String number, Long id) {
        return persistenceRepository.existsByPropertyIdAndRoomNumberIgnoreCaseAndIdIsNot(propertyId, number, id);
    }

    @Override
    public boolean existsByRoomTypeId(Long roomTypeId) {
        return persistenceRepository.existsByRoomTypeId(roomTypeId);
    }

    @Override
    public Room save(Room room) {
        var saved = persistenceRepository.save(RoomPersistenceAssembler.toPersistenceFromDomain(room));
        domainEventPublisher.publishAndClearEvents(room);
        return RoomPersistenceAssembler.toDomainFromPersistence(saved);
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
