package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.adapters;

import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.PaymentRepository;
import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.assemblers.PaymentPersistenceAssembler;
import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.repositories.PaymentPersistenceRepository;
import com.teamcoworkers.hostera.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PaymentRepositoryImpl(PaymentPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return persistenceRepository.findById(id).map(PaymentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Payment> findAll() {
        return persistenceRepository.findAll(Sort.by("id")).stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Payment> findAllByPropertyId(Long propertyId) {
        return persistenceRepository.findAllByPropertyIdOrderByIdAsc(propertyId).stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public List<Payment> findAllByBookingId(Long bookingId) {
        return persistenceRepository.findAllByBookingIdOrderByIdAsc(bookingId).stream()
                .map(PaymentPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public Payment save(Payment payment) {
        var saved = persistenceRepository.save(PaymentPersistenceAssembler.toPersistenceFromDomain(payment));
        domainEventPublisher.publishAndClearEvents(payment);
        return PaymentPersistenceAssembler.toDomainFromPersistence(saved);
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
