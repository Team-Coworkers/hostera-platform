package com.teamcoworkers.hostera.platform.organizations.domain.repositories;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository of {@link StaffMember} aggregates.
 */
public interface StaffMemberRepository {
    Optional<StaffMember> findById(Long id);
    List<StaffMember> findAll();
    List<StaffMember> findAllByPropertyId(Long propertyId);
    boolean existsById(Long id);
    boolean existsByPropertyId(Long propertyId);
    StaffMember save(StaffMember staffMember);
    void deleteById(Long id);
}
