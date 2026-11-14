package com.teamcoworkers.hostera.platform.organizations.application.commandservices;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.DeleteStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.UpdateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface StaffMemberCommandService {
    Result<StaffMember, ApplicationError> handle(CreateStaffMemberCommand command);
    Result<StaffMember, ApplicationError> handle(UpdateStaffMemberCommand command);
    Result<Long, ApplicationError> handle(DeleteStaffMemberCommand command);
}
