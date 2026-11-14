package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.assemblers.BookingPersistenceAssembler;
import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.repositories.BookingPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public BookingRepositoryImpl(BookingPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return persistenceRepository.findById(id).map(BookingPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Booking> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream()
                .map(BookingPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Booking> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(BookingPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Booking> findAllByRoomId(Long roomId) {
        return persistenceRepository.findAllByRoomIdOrderByIdAsc(roomId).stream()
                .map(BookingPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public Booking save(Booking booking) {
        var saved = persistenceRepository.save(BookingPersistenceAssembler.toPersistenceFromDomain(booking));
        domainEventPublisher.publishAndClearEvents(booking);
        return BookingPersistenceAssembler.toDomainFromPersistence(saved);
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
