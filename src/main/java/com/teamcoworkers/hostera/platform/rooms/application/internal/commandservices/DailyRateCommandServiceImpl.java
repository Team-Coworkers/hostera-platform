package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.DailyRateCommandService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.DateRange;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.DailyRateRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DailyRateCommandServiceImpl implements DailyRateCommandService {
    private static final String DUPLICATED_PRICE = "The rate plan already has a price for that room type and date";

    private final DailyRateRepository dailyRateRepository;
    private final RatePlanRepository ratePlanRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public DailyRateCommandServiceImpl(DailyRateRepository dailyRateRepository, RatePlanRepository ratePlanRepository,
                                       RoomTypeRepository roomTypeRepository,
                                       OrganizationsContextFacade organizationsContextFacade) {
        this.dailyRateRepository = dailyRateRepository;
        this.ratePlanRepository = ratePlanRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<DailyRate, ApplicationError> handle(CreateDailyRateCommand command) {
        var referenceError = findReferenceError(command.propertyId(), command.ratePlanId(), command.roomTypeId());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        try {
            var date = DateRange.parseDay(command.date(), "Date");
            if (dailyRateRepository.existsByRatePlanIdAndRoomTypeIdAndDate(command.ratePlanId(), command.roomTypeId(), date))
                return Result.failure(ApplicationError.conflict("Daily_Rate", DUPLICATED_PRICE));
            var dailyRate = new DailyRate(command.propertyId(), command.ratePlanId(), command.roomTypeId(), date,
                    new NightlyRate(command.amount()));
            return Result.success(dailyRateRepository.save(dailyRate));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("dailyRate", e.getMessage()));
        }
    }

    @Override
    public Result<DailyRate, ApplicationError> handle(UpdateDailyRateCommand command) {
        var existing = dailyRateRepository.findById(command.dailyRateId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Daily_Rate", command.dailyRateId().toString()));
        var referenceError = findReferenceError(command.propertyId(), command.ratePlanId(), command.roomTypeId());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        try {
            var date = DateRange.parseDay(command.date(), "Date");
            if (dailyRateRepository.existsByRatePlanIdAndRoomTypeIdAndDateAndIdIsNot(command.ratePlanId(), command.roomTypeId(), date, command.dailyRateId()))
                return Result.failure(ApplicationError.conflict("Daily_Rate", DUPLICATED_PRICE));
            var dailyRate = existing.get().updateInformation(command.propertyId(), command.ratePlanId(), command.roomTypeId(),
                    date, new NightlyRate(command.amount()));
            return Result.success(dailyRateRepository.save(dailyRate));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("dailyRate", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteDailyRateCommand command) {
        if (!dailyRateRepository.existsById(command.dailyRateId()))
            return Result.failure(ApplicationError.notFound("Daily_Rate", command.dailyRateId().toString()));
        dailyRateRepository.deleteById(command.dailyRateId());
        return Result.success(command.dailyRateId());
    }

    /** The property exists, and the rate plan and the room type belong to it. */
    private Optional<ApplicationError> findReferenceError(Long propertyId, Long ratePlanId, Long roomTypeId) {
        if (!organizationsContextFacade.existsPropertyById(propertyId))
            return Optional.of(ApplicationError.notFound("Property", String.valueOf(propertyId)));
        var ratePlanOfProperty = ratePlanId != null && ratePlanRepository.findById(ratePlanId)
                .map(ratePlan -> ratePlan.getPropertyId().equals(propertyId)).orElse(false);
        if (!ratePlanOfProperty)
            return Optional.of(ApplicationError.notFound("Rate_Plan", String.valueOf(ratePlanId)));
        var roomTypeOfProperty = roomTypeId != null && roomTypeRepository.findById(roomTypeId)
                .map(roomType -> roomType.getPropertyId().equals(propertyId)).orElse(false);
        if (!roomTypeOfProperty)
            return Optional.of(ApplicationError.notFound("Room_Type", String.valueOf(roomTypeId)));
        return Optional.empty();
    }
}
