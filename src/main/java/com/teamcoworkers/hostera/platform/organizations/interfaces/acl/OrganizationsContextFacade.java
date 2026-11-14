package com.teamcoworkers.hostera.platform.organizations.interfaces.acl;

import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import org.springframework.stereotype.Service;

/**
 * Anti-corruption layer facade that other bounded contexts use to check properties and staff
 * without depending on the organizations domain model.
 */
@Service
public class OrganizationsContextFacade {
    private final PropertyRepository propertyRepository;
    private final StaffMemberRepository staffMemberRepository;

    public OrganizationsContextFacade(PropertyRepository propertyRepository, StaffMemberRepository staffMemberRepository) {
        this.propertyRepository = propertyRepository;
        this.staffMemberRepository = staffMemberRepository;
    }

    /** @return whether a property with the given id exists */
    public boolean existsPropertyById(Long propertyId) {
        return propertyId != null && propertyRepository.existsById(propertyId);
    }

    /** @return whether the staff member exists and works at the given property */
    public boolean isStaffMemberOfProperty(Long staffMemberId, Long propertyId) {
        return staffMemberId != null && staffMemberRepository.findById(staffMemberId)
                .map(member -> member.getPropertyId().equals(propertyId))
                .orElse(false);
    }
}
