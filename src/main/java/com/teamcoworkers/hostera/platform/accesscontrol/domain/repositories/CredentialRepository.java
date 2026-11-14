package com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.Credential;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository {

    Optional<Credential> findById(Long id);

    List<Credential> findAll();

    List<Credential> findAllByPropertyId(Long propertyId);

    List<Credential> findAllByBookingId(Long bookingId);

    boolean existsById(Long id);

    Credential save(Credential credential);

    void deleteById(Long id);

    long count();
}
