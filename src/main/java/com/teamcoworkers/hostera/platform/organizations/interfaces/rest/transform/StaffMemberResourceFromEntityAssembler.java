package com.teamcoworkers.hostera.platform.organizations.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.UpdateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.SaveStaffMemberResource;
import com.teamcoworkers.hostera.platform.organizations.interfaces.rest.resources.StaffMemberResource;

public final class StaffMemberResourceFromEntityAssembler {
    private StaffMemberResourceFromEntityAssembler() {
    }

    public static StaffMemberResource toResourceFromEntity(StaffMember staffMember) {
        return new StaffMemberResource(staffMember.getId(), staffMember.getPropertyId(), staffMember.getName(), staffMember.getRole());
    }

    public static CreateStaffMemberCommand toCreateCommand(SaveStaffMemberResource resource) {
        return new CreateStaffMemberCommand(resource.propertyId(), resource.name(), resource.role());
    }

    public static UpdateStaffMemberCommand toUpdateCommand(Long staffMemberId, SaveStaffMemberResource resource) {
        return new UpdateStaffMemberCommand(staffMemberId, resource.propertyId(), resource.name(), resource.role());
    }
}
