package com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Staff member aggregate root: a person who works at one property (front desk, housekeeping,
 * maintenance or management) and can be named as the holder of a staff credential.
 * <p>
 * Invariants: it belongs to an existing property and has a name and a role.
 * </p>
 */
@Getter
public class StaffMember extends AbstractDomainAggregateRoot<StaffMember> {
    private Long id;
    private Long propertyId;
    private String name;
    private String role;

    public StaffMember(Long propertyId, String name, String role) {
        updateInformation(propertyId, name, role);
    }

    public static StaffMember reconstitute(Long id, Long propertyId, String name, String role) {
        var staffMember = new StaffMember(propertyId, name, role);
        staffMember.id = id;
        return staffMember;
    }

    public StaffMember updateInformation(Long propertyId, String name, String role) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Staff member name is required");
        if (role == null || role.isBlank()) throw new IllegalArgumentException("Staff member role is required");
        this.propertyId = propertyId;
        this.name = name.trim();
        this.role = role.trim();
        return this;
    }
}
