package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.entities.CredentialPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialPersistenceRepository extends JpaRepository<CredentialPersistenceEntity, Long> {

    List<CredentialPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);

    List<CredentialPersistenceEntity> findAllByBookingIdOrderByIdAsc(Long bookingId);
}
