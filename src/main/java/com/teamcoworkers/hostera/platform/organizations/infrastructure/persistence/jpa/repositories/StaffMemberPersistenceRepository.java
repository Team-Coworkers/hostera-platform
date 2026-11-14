package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.repositories;

import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.entities.StaffMemberPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffMemberPersistenceRepository extends JpaRepository<StaffMemberPersistenceEntity, Long> {
    List<StaffMemberPersistenceEntity> findAllByPropertyIdOrderByIdAsc(Long propertyId);
    boolean existsByPropertyId(Long propertyId);
}
