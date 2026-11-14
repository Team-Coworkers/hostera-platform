package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.DeleteRoomTypeCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomTypeCommandServiceImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @InjectMocks
    private RoomTypeCommandServiceImpl service;

    @Test
    void createsTheRoomTypeWhenThePropertyExists() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.existsByPropertyIdAndName(1L, "Standard")).thenReturn(false);
        when(roomTypeRepository.save(any(RoomType.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateRoomTypeCommand(1L, "Standard", 2, "1 queen", new BigDecimal("180"), "active"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getBaseNightlyRate().amount()).isEqualByComparingTo("180");
    }

    @Test
    void rejectsABaseNightlyRateOfZero() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomTypeRepository.existsByPropertyIdAndName(1L, "Standard")).thenReturn(false);

        var result = service.handle(new CreateRoomTypeCommand(1L, "Standard", 2, "1 queen", BigDecimal.ZERO, "active"));

        assertThat(result.isFailure()).isTrue();
        verify(roomTypeRepository, never()).save(any());
    }

    @Test
    void failsWhenThePropertyDoesNotExist() {
        when(organizationsContextFacade.existsPropertyById(9L)).thenReturn(false);

        var result = service.handle(new CreateRoomTypeCommand(9L, "Standard", 2, "1 queen", new BigDecimal("180"), "active"));

        assertThat(result.isFailure()).isTrue();
        verify(roomTypeRepository, never()).save(any());
    }

    @Test
    void doesNotDeleteARoomTypeThatStillHasRooms() {
        when(roomTypeRepository.existsById(1L)).thenReturn(true);
        when(roomRepository.existsByRoomTypeId(1L)).thenReturn(true);

        var result = service.handle(new DeleteRoomTypeCommand(1L));

        assertThat(result.isFailure()).isTrue();
        verify(roomTypeRepository, never()).deleteById(anyLong());
    }
}
