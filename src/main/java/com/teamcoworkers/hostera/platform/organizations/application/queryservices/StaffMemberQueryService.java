package com.teamcoworkers.hostera.platform.organizations.application.queryservices;

import com.teamcoworkers.hostera.platform.organizations.application.queries.GetAllStaffMembersQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetStaffMemberByIdQuery;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;

import java.util.List;
import java.util.Optional;

public interface StaffMemberQueryService {
    List<StaffMember> handle(GetAllStaffMembersQuery query);
    Optional<StaffMember> handle(GetStaffMemberByIdQuery query);
}
