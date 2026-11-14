package com.teamcoworkers.hostera.platform.organizations.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreatePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.DeletePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commands.UpdatePropertyCommand;
import com.teamcoworkers.hostera.platform.organizations.application.commandservices.PropertyCommandService;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.Property;
import com.teamcoworkers.hostera.platform.organizations.domain.model.valueobjects.CurrencyCode;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class PropertyCommandServiceImpl implements PropertyCommandService {
    private final PropertyRepository propertyRepository;
    private final StaffMemberRepository staffMemberRepository;

    public PropertyCommandServiceImpl(PropertyRepository propertyRepository, StaffMemberRepository staffMemberRepository) {
        this.propertyRepository = propertyRepository;
        this.staffMemberRepository = staffMemberRepository;
    }

    @Override
    public Result<Property, ApplicationError> handle(CreatePropertyCommand command) {
        if (command.name() != null && propertyRepository.existsByOrganizationIdAndName(command.organizationId(), command.name().trim()))
            return Result.failure(ApplicationError.conflict("Property", "Name '%s' already exists in the organization".formatted(command.name())));
        try {
            var property = new Property(command.organizationId(), command.name(), command.city(), new CurrencyCode(command.currency()));
            return Result.success(propertyRepository.save(property));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("property", e.getMessage()));
        }
    }

    @Override
    public Result<Property, ApplicationError> handle(UpdatePropertyCommand command) {
        var existing = propertyRepository.findById(command.propertyId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Property", command.propertyId().toString()));
        if (command.name() != null && propertyRepository.existsByOrganizationIdAndNameAndIdIsNot(command.organizationId(), command.name().trim(), command.propertyId()))
            return Result.failure(ApplicationError.conflict("Property", "Name '%s' already exists in the organization".formatted(command.name())));
        try {
            var property = existing.get().updateInformation(command.organizationId(), command.name(), command.city(), new CurrencyCode(command.currency()));
            return Result.success(propertyRepository.save(property));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("property", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeletePropertyCommand command) {
        if (!propertyRepository.existsById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", command.propertyId().toString()));
        if (staffMemberRepository.existsByPropertyId(command.propertyId()))
            return Result.failure(ApplicationError.businessRuleViolation("property-has-staff", "Reassign or remove the staff members of the property first"));
        propertyRepository.deleteById(command.propertyId());
        return Result.success(command.propertyId());
    }
}
