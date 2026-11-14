package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateStatusPeriodCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.OperationalStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.StatusPeriodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusPeriodCommandServiceImplTest {

    @Mock
    private StatusPeriodRepository statusPeriodRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @InjectMocks
    private StatusPeriodCommandServiceImpl service;

    private static Room roomOf(Long propertyId) {
        return Room.reconstitute(12L, propertyId, "412", 1L, 4, RoomStatus.AVAILABLE);
    }

    @Test
    void createsTheStatusPeriodOfARoomOfTheProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomRepository.findById(12L)).thenReturn(Optional.of(roomOf(1L)));
        when(statusPeriodRepository.save(any(StatusPeriod.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateStatusPeriodCommand(1L, 12L, "blocked", "2026-10-10", "2026-10-11", "Held for a corporate group"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getStatus()).isEqualTo(OperationalStatus.BLOCKED);
    }

    @Test
    void rejectsAnEndDateBeforeTheStartDate() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomRepository.findById(12L)).thenReturn(Optional.of(roomOf(1L)));

        var result = service.handle(new CreateStatusPeriodCommand(1L, 12L, "blocked", "2026-10-11", "2026-10-10", "Held for a corporate group"));

        assertThat(result.isFailure()).isTrue();
        verify(statusPeriodRepository, never()).save(any());
    }

    @Test
    void rejectsARoomOfAnotherProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(roomRepository.findById(12L)).thenReturn(Optional.of(roomOf(2L)));

        var result = service.handle(new CreateStatusPeriodCommand(1L, 12L, "blocked", "2026-10-10", "2026-10-11", "Held for a corporate group"));

        assertThat(result.isFailure()).isTrue();
        verify(statusPeriodRepository, never()).save(any());
    }
}
