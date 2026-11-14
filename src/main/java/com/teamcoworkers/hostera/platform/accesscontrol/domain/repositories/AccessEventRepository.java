package com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories;

import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;

import java.util.List;
import java.util.Optional;

public interface AccessEventRepository {

    Optional<AccessEvent> findById(Long id);

    List<AccessEvent> findAll();

    List<AccessEvent> findAllByPropertyId(Long propertyId);

    boolean existsById(Long id);

    AccessEvent save(AccessEvent accessEvent);

    void deleteById(Long id);

    long count();
}
