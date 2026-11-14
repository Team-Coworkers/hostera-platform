package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomCommandServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @InjectMocks
    private RoomCommandServiceImpl service;

    private static RoomType standardRoomTypeOf(Long propertyId) {
        return RoomType.reconstitute(1L, propertyId, "Standard", 2, "1 queen", new NightlyRate(new BigDecimal("180")), ActivationStatus.ACTIVE);
    }

    @Test
    void createsARoomThatStartsAvailable() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(standardRoomTypeOf(1L)));
        when(roomRepository.existsByPropertyIdAndNumber(1L, "201")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateRoomCommand(1L, "201", 1L, 2, null));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getStatus()).isEqualTo(RoomStatus.AVAILABLE);
    }

    @Test
    void rejectsANumberThatAlreadyExistsInTheProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(standardRoomTypeOf(1L)));
        when(roomRepository.existsByPropertyIdAndNumber(1L, "201")).thenReturn(true);

        var result = service.handle(new CreateRoomCommand(1L, "201", 1L, 2, null));

        assertThat(result.isFailure()).isTrue();
        verify(roomRepository, never()).save(any());
    }

    @Test
    void rejectsARoomTypeOfAnotherProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(standardRoomTypeOf(2L)));

        var result = service.handle(new CreateRoomCommand(1L, "201", 1L, 2, null));

        assertThat(result.isFailure()).isTrue();
        verify(roomRepository, never()).save(any());
    }

    @Test
    void theCheckOutLeavesTheRoomNeedingCleaning() {
        when(roomRepository.findById(7L)).thenReturn(Optional.of(Room.reconstitute(7L, 1L, "201", 1L, 2, RoomStatus.OCCUPIED)));
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(standardRoomTypeOf(1L)));
        when(roomRepository.existsByPropertyIdAndNumberAndIdIsNot(1L, "201", 7L)).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new UpdateRoomCommand(7L, 1L, "201", 1L, 2, "needs-cleaning"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getStatus()).isEqualTo(RoomStatus.NEEDS_CLEANING);
    }

    @Test
    void anOccupiedRoomCannotBeMarkedAsReady() {
        when(roomRepository.findById(7L)).thenReturn(Optional.of(Room.reconstitute(7L, 1L, "201", 1L, 2, RoomStatus.OCCUPIED)));
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(standardRoomTypeOf(1L)));
        when(roomRepository.existsByPropertyIdAndNumberAndIdIsNot(1L, "201", 7L)).thenReturn(false);

        var result = service.handle(new UpdateRoomCommand(7L, 1L, "201", 1L, 2, "ready"));

        assertThat(result.isFailure()).isTrue();
        verify(roomRepository, never()).save(any());
    }

    @Test
    void keepsTheStatusWhenTheUpdateDoesNotSendIt() {
        when(roomRepository.findById(7L)).thenReturn(Optional.of(Room.reconstitute(7L, 1L, "201", 1L, 2, RoomStatus.READY)));
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(standardRoomTypeOf(1L)));
        when(roomRepository.existsByPropertyIdAndNumberAndIdIsNot(1L, "202", 7L)).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new UpdateRoomCommand(7L, 1L, "202", 1L, 2, null));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getNumber()).isEqualTo("202");
        assertThat(result.toOptional().orElseThrow().getStatus()).isEqualTo(RoomStatus.READY);
    }
}
