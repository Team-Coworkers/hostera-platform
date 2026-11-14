package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RoomCommandService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomCommandServiceImpl implements RoomCommandService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public RoomCommandServiceImpl(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository,
                                  OrganizationsContextFacade organizationsContextFacade) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<Room, ApplicationError> handle(CreateRoomCommand command) {
        var referenceError = findReferenceError(command.propertyId(), command.roomTypeId());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        if (command.number() != null && roomRepository.existsByPropertyIdAndNumber(command.propertyId(), command.number().trim()))
            return Result.failure(ApplicationError.conflict("Room", "Number '%s' already exists in the property".formatted(command.number())));
        try {
            var room = new Room(command.propertyId(), command.number(), command.roomTypeId(), command.floor());
            if (hasText(command.status())) room.changeStatus(RoomStatus.fromValue(command.status()));
            return Result.success(roomRepository.save(room));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("room", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("room-status-transition", e.getMessage()));
        }
    }

    @Override
    public Result<Room, ApplicationError> handle(UpdateRoomCommand command) {
        var existing = roomRepository.findById(command.roomId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Room", command.roomId().toString()));
        var referenceError = findReferenceError(command.propertyId(), command.roomTypeId());
        if (referenceError.isPresent()) return Result.failure(referenceError.get());
        if (command.number() != null && roomRepository.existsByPropertyIdAndNumberAndIdIsNot(command.propertyId(), command.number().trim(), command.roomId()))
            return Result.failure(ApplicationError.conflict("Room", "Number '%s' already exists in the property".formatted(command.number())));
        try {
            var room = existing.get().updateInformation(command.propertyId(), command.number(), command.roomTypeId(), command.floor());
            if (hasText(command.status())) room.changeStatus(RoomStatus.fromValue(command.status()));
            return Result.success(roomRepository.save(room));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("room", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("room-status-transition", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteRoomCommand command) {
        var existing = roomRepository.findById(command.roomId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Room", command.roomId().toString()));
        if (existing.get().getStatus() == RoomStatus.OCCUPIED)
            return Result.failure(ApplicationError.businessRuleViolation("room-is-occupied", "An occupied room cannot be deleted"));
        roomRepository.deleteById(command.roomId());
        return Result.success(command.roomId());
    }

    /** The property exists and the room type belongs to it. */
    private Optional<ApplicationError> findReferenceError(Long propertyId, Long roomTypeId) {
        if (!organizationsContextFacade.existsPropertyById(propertyId))
            return Optional.of(ApplicationError.notFound("Property", String.valueOf(propertyId)));
        var roomType = roomTypeId == null ? Optional.<RoomType>empty() : roomTypeRepository.findById(roomTypeId);
        if (roomType.isEmpty())
            return Optional.of(ApplicationError.notFound("Room_Type", String.valueOf(roomTypeId)));
        if (!roomType.get().getPropertyId().equals(propertyId))
            return Optional.of(ApplicationError.businessRuleViolation("room-type-of-another-property", "The room type belongs to another property"));
        return Optional.empty();
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
