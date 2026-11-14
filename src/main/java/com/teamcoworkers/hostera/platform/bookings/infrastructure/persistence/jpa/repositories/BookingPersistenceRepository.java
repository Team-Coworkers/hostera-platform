package com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.bookings.infrastructure.persistence.jpa.entities.BookingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingPersistenceRepository extends JpaRepository<BookingPersistenceEntity, Long> {
    List<BookingPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);

    List<BookingPersistenceEntity> findAllByRoomIdOrderByIdAsc(Long roomId);
}
