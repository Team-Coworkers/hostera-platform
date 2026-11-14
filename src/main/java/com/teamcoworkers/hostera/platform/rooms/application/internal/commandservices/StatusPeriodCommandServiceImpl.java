package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.StatusPeriodCommandService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.DateRange;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.OperationalStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.StatusPeriodRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusPeriodCommandServiceImpl implements StatusPeriodCommandService {
    private final StatusPeriodRepository statusPeriodRepository;
    private final RoomRepository roomRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public StatusPeriodCommandServiceImpl(StatusPeriodRepository statusPeriodRepository, RoomRepository roomRepository,
                                          OrganizationsContextFacade organizationsContextFacade) {
        this.statusPeriodRepository = statusPeriodRepository;
        this.roomRepository = roomRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<StatusPeriod, ApplicationError> handle(CreateStatusPeriodCommand command) {
        var referenceError = findReferenceError(command.propertyId(), command.roomId());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        try {
            var statusPeriod = new StatusPeriod(command.propertyId(), command.roomId(), OperationalStatus.fromValue(command.status()),
                    DateRange.of(command.startDate(), command.endDate()), command.reason());
            return Result.success(statusPeriodRepository.save(statusPeriod));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("statusPeriod", e.getMessage()));
        }
    }

    @Override
    public Result<StatusPeriod, ApplicationError> handle(UpdateStatusPeriodCommand command) {
        var existing = statusPeriodRepository.findById(command.statusPeriodId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Status_Period", command.statusPeriodId().toString()));
        var referenceError = findReferenceError(command.propertyId(), command.roomId());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        try {
            var statusPeriod = existing.get().updateInformation(command.propertyId(), command.roomId(),
                    OperationalStatus.fromValue(command.status()), DateRange.of(command.startDate(), command.endDate()), command.reason());
            return Result.success(statusPeriodRepository.save(statusPeriod));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("statusPeriod", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteStatusPeriodCommand command) {
        if (!statusPeriodRepository.existsById(command.statusPeriodId()))
            return Result.failure(ApplicationError.notFound("Status_Period", command.statusPeriodId().toString()));
        statusPeriodRepository.deleteById(command.statusPeriodId());
        return Result.success(command.statusPeriodId());
    }

    /** The property exists and the room belongs to it. */
    private Optional<ApplicationError> findReferenceError(Long propertyId, Long roomId) {
        if (!organizationsContextFacade.existsPropertyById(propertyId))
            return Optional.of(ApplicationError.notFound("Property", String.valueOf(propertyId)));
        var roomOfProperty = roomId != null && roomRepository.findById(roomId)
                .map(room -> room.getPropertyId().equals(propertyId)).orElse(false);
        if (!roomOfProperty)
            return Optional.of(ApplicationError.notFound("Room", String.valueOf(roomId)));
        return Optional.empty();
    }
}
