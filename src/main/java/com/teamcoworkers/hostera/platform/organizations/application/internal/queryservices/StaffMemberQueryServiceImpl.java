package com.teamcoworkers.hostera.platform.organizations.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.organizations.application.queries.GetAllStaffMembersQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queries.GetStaffMemberByIdQuery;
import com.teamcoworkers.hostera.platform.organizations.application.queryservices.StaffMemberQueryService;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffMemberQueryServiceImpl implements StaffMemberQueryService {
    private final StaffMemberRepository staffMemberRepository;

    public StaffMemberQueryServiceImpl(StaffMemberRepository staffMemberRepository) {
        this.staffMemberRepository = staffMemberRepository;
    }

    @Override
    public List<StaffMember> handle(GetAllStaffMembersQuery query) {
        return query.propertyId() == null
                ? staffMemberRepository.findAll()
                : staffMemberRepository.findAllByPropertyId(query.propertyId());
    }

    @Override
    public Optional<StaffMember> handle(GetStaffMemberByIdQuery query) {
        return staffMemberRepository.findById(query.staffMemberId());
    }
}
