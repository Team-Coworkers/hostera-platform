package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRatePlanCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RatePlanCommandService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.IncludedServices;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.DailyRateRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatePlanCommandServiceImpl implements RatePlanCommandService {
    private final RatePlanRepository ratePlanRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final DailyRateRepository dailyRateRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public RatePlanCommandServiceImpl(RatePlanRepository ratePlanRepository, RoomTypeRepository roomTypeRepository,
                                      DailyRateRepository dailyRateRepository,
                                      OrganizationsContextFacade organizationsContextFacade) {
        this.ratePlanRepository = ratePlanRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.dailyRateRepository = dailyRateRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<RatePlan, ApplicationError> handle(CreateRatePlanCommand command) {
        var referenceError = findReferenceError(command.propertyId(), command.roomTypeIds());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        if (command.name() != null && ratePlanRepository.existsByPropertyIdAndName(command.propertyId(), command.name().trim()))
            return Result.failure(ApplicationError.conflict("Rate_Plan", "Name '%s' already exists in the property".formatted(command.name())));
        try {
            var ratePlan = new RatePlan(command.propertyId(), command.name(), command.roomTypeIds(),
                    IncludedServices.fromValue(command.includedServices()), command.refundable(),
                    command.cancellationPolicy(), ActivationStatus.fromValue(command.status()));
            return Result.success(ratePlanRepository.save(ratePlan));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("ratePlan", e.getMessage()));
        }
    }

    @Override
    public Result<RatePlan, ApplicationError> handle(UpdateRatePlanCommand command) {
        var existing = ratePlanRepository.findById(command.ratePlanId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Rate_Plan", command.ratePlanId().toString()));
        var referenceError = findReferenceError(command.propertyId(), command.roomTypeIds());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        if (command.name() != null && ratePlanRepository.existsByPropertyIdAndNameAndIdIsNot(command.propertyId(), command.name().trim(), command.ratePlanId()))
            return Result.failure(ApplicationError.conflict("Rate_Plan", "Name '%s' already exists in the property".formatted(command.name())));
        try {
            var ratePlan = existing.get().updateInformation(command.propertyId(), command.name(), command.roomTypeIds(),
                    IncludedServices.fromValue(command.includedServices()), command.refundable(),
                    command.cancellationPolicy(), ActivationStatus.fromValue(command.status()));
            return Result.success(ratePlanRepository.save(ratePlan));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("ratePlan", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteRatePlanCommand command) {
        if (!ratePlanRepository.existsById(command.ratePlanId()))
            return Result.failure(ApplicationError.notFound("Rate_Plan", command.ratePlanId().toString()));
        if (dailyRateRepository.existsByRatePlanId(command.ratePlanId()))
            return Result.failure(ApplicationError.businessRuleViolation("rate-plan-has-daily-rates", "Delete the daily rates of the rate plan first"));
        ratePlanRepository.deleteById(command.ratePlanId());
        return Result.success(command.ratePlanId());
    }

    /** The property exists and every listed room type belongs to it. */
    private Optional<ApplicationError> findReferenceError(Long propertyId, List<Long> roomTypeIds) {
        if (!organizationsContextFacade.existsPropertyById(propertyId))
            return Optional.of(ApplicationError.notFound("Property", String.valueOf(propertyId)));
        if (roomTypeIds == null) return Optional.empty();
        for (var roomTypeId : roomTypeIds) {
            var roomType = roomTypeId == null ? Optional.<RoomType>empty() : roomTypeRepository.findById(roomTypeId);
            if (roomType.isEmpty() || !roomType.get().getPropertyId().equals(propertyId))
                return Optional.of(ApplicationError.notFound("Room_Type", String.valueOf(roomTypeId)));
        }
        return Optional.empty();
    }
}
