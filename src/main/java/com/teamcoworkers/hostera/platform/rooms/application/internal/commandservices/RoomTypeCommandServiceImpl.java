package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RoomTypeCommandService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeCommandServiceImpl implements RoomTypeCommandService {
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public RoomTypeCommandServiceImpl(RoomTypeRepository roomTypeRepository, RoomRepository roomRepository,
                                      OrganizationsContextFacade organizationsContextFacade) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<RoomType, ApplicationError> handle(CreateRoomTypeCommand command) {
        if (!organizationsContextFacade.existsPropertyById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        if (command.name() != null && roomTypeRepository.existsByPropertyIdAndName(command.propertyId(), command.name().trim()))
            return Result.failure(ApplicationError.conflict("Room_Type", "Name '%s' already exists in the property".formatted(command.name())));
        try {
            var roomType = new RoomType(command.propertyId(), command.name(), command.capacity(), command.bedConfiguration(),
                    new NightlyRate(command.baseNightlyRate()), ActivationStatus.fromValue(command.status()));
            return Result.success(roomTypeRepository.save(roomType));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("roomType", e.getMessage()));
        }
    }

    @Override
    public Result<RoomType, ApplicationError> handle(UpdateRoomTypeCommand command) {
        var existing = roomTypeRepository.findById(command.roomTypeId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Room_Type", command.roomTypeId().toString()));
        if (!organizationsContextFacade.existsPropertyById(command.propertyId()))
            return Result.failure(ApplicationError.notFound("Property", String.valueOf(command.propertyId())));
        if (command.name() != null && roomTypeRepository.existsByPropertyIdAndNameAndIdIsNot(command.propertyId(), command.name().trim(), command.roomTypeId()))
            return Result.failure(ApplicationError.conflict("Room_Type", "Name '%s' already exists in the property".formatted(command.name())));
        try {
            var roomType = existing.get().updateInformation(command.propertyId(), command.name(), command.capacity(),
                    command.bedConfiguration(), new NightlyRate(command.baseNightlyRate()), ActivationStatus.fromValue(command.status()));
            return Result.success(roomTypeRepository.save(roomType));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("roomType", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeleteRoomTypeCommand command) {
        if (!roomTypeRepository.existsById(command.roomTypeId()))
            return Result.failure(ApplicationError.notFound("Room_Type", command.roomTypeId().toString()));
        if (roomRepository.existsByRoomTypeId(command.roomTypeId()))
            return Result.failure(ApplicationError.businessRuleViolation("room-type-has-rooms", "Move the rooms of the room type to another room type first"));
        roomTypeRepository.deleteById(command.roomTypeId());
        return Result.success(command.roomTypeId());
    }
}
