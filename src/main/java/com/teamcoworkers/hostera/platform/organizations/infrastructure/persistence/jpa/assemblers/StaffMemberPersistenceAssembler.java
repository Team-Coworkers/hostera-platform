package com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.assemblers;

import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.infrastructure.persistence.jpa.entities.StaffMemberPersistenceEntity;

public final class StaffMemberPersistenceAssembler {
    private StaffMemberPersistenceAssembler() {
    }

    public static StaffMember toDomainFromPersistence(StaffMemberPersistenceEntity entity) {
        if (entity == null) return null;
        return StaffMember.reconstitute(entity.getId(), entity.getPropertyId(), entity.getName(), entity.getRole());
    }

    public static StaffMemberPersistenceEntity toPersistenceFromDomain(StaffMember staffMember) {
        var entity = new StaffMemberPersistenceEntity();
        if (staffMember.getId() != null) entity.setId(staffMember.getId());
        entity.setPropertyId(staffMember.getPropertyId());
        entity.setName(staffMember.getName());
        entity.setRole(staffMember.getRole());
        return entity;
    }
}
