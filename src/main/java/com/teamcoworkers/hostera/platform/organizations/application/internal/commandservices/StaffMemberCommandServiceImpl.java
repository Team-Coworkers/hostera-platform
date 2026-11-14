package com.teamcoworkers.hostera.platform.organizations.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.DeleteStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.UpdateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commandservices.StaffMemberCommandService;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class StaffMemberCommandServiceImpl implements StaffMemberCommandService {
    private final StaffMemberRepository staffMemberRepository;
    private final PropertyRepository propertyRepository;

    public StaffMemberCommandServiceImpl(StaffMemberRepository staffMemberRepository, PropertyRepository propertyRepository) {
        this.staffMemberRepository = staffMemberRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Result<StaffMember, ApplicationError> handle(CreateStaffMemberCommand command) {
        if (command.propertyId() == null || !propertyRepository.existsById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        try {
            return Result.success(staffMemberRepository.save(new StaffMember(command.propertyId(), command.name(), command.role())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("staffMember", e.getMessage()));
        }
    }

    @Override
    public Result<StaffMember, ApplicationError> handle(UpdateStaffMemberCommand command) {
        var existing = staffMemberRepository.findById(command.staffMemberId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Staff_Member", command.staffMemberId().toString()));
        if (command.propertyId() == null || !propertyRepository.existsById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        try {
            var staffMember = existing.get().updateInformation(command.propertyId(), command.name(), command.role());
            return Result.success(staffMemberRepository.save(staffMember));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("staffMember", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteStaffMemberCommand command) {
        if (!staffMemberRepository.existsById(command.staffMemberId()))
            return Result.failure(ApplicationError.notFound("Staff_Member", command.staffMemberId().toString()));
        staffMemberRepository.deleteById(command.staffMemberId());
        return Result.success(command.staffMemberId());
    }
}
