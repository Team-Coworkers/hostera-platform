package com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.accesscontrol.infrastructure.persistence.jpa.entities.AccessEventPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessEventPersistenceRepository extends JpaRepository<AccessEventPersistenceEntity, Long> {

    List<AccessEventPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
}
