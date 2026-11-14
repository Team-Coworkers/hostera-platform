package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.entities.PropertyPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyPersistenceRepository extends JpaRepository<PropertyPersistenceEntity, Long> {
    List<PropertyPersistenceEntity> findAllByOrganizationIdOrderByIdAsc(Long organizationId);
    boolean existsByOrganizationIdAndNameIgnoreCase(Long organizationId, String name);
    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdIsNot(Long organizationId, String name, Long id);
}
