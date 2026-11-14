package com.teamcoworkers.hostera.platform.organizations.domain.repositories;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link Property} aggregates.
 */
public interface PropertyRepository {
    Optional<Property> findById(Long id);
    List<Property> findAll();
    List<Property> findAllByOrganizationId(Long organizationId);
    boolean existsById(Long id);
    boolean existsByOrganizationIdAndName(Long organizationId, String name);
    boolean existsByOrganizationIdAndNameAndIdIsNot(Long organizationId, String name, Long id);
    Property save(Property property);
    void deleteById(Long id);
    long count();
}
