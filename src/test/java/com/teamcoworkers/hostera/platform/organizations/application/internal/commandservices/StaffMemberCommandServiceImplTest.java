package com.teamcoworkers.hostera.platform.organizations.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.application.commands.CreateStaffMemberCommand;
import com.teamcoworkers.hostera.platform.organizations.domain.model.aggregates.StaffMember;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.PropertyRepository;
import com.teamcoworkers.hostera.platform.organizations.domain.repositories.StaffMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StaffMemberCommandServiceImplTest {

    @Mock
    private StaffMemberRepository staffMemberRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private StaffMemberCommandServiceImpl service;

    @Test
    void createsTheStaffMemberWhenThePropertyExists() {
        when(propertyRepository.existsById(1L)).thenReturn(true);
        when(staffMemberRepository.save(any(StaffMember.class)))
                .thenAnswer(call -> StaffMember.reconstitute(5L, 1L, "Rosa Pérez", "Housekeeping supervisor"));

        var result = service.handle(new CreateStaffMemberCommand(1L, "Rosa Pérez", "Housekeeping supervisor"));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getId()).isEqualTo(5L);
    }

    @Test
    void failsWhenThePropertyDoesNotExist() {
        when(propertyRepository.existsById(9L)).thenReturn(false);

        var result = service.handle(new CreateStaffMemberCommand(9L, "Rosa Pérez", "Housekeeping supervisor"));

        assertThat(result.isFailure()).isTrue();
        verify(staffMemberRepository, never()).save(any());
    }
}
