package com.teamcoworkers.hostera.platform.accesscontrol.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.AccessEventData;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateAccessEventCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessResult;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.DenialReason;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.AccessEventRepository;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.repositories.CredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.DURING;
import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.guestCard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessEventCommandServiceImplTest {

    @Mock
    private AccessEventRepository accessEventRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @InjectMocks
    private AccessEventCommandServiceImpl service;

    @Test
    void decidesTheResultFromTheCredential() {
        when(credentialRepository.findById(1L)).thenReturn(Optional.of(guestCard(1L, 6L, null)));
        when(accessEventRepository.save(any(AccessEvent.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateAccessEventCommand(new AccessEventData(1L, DURING, 1L, "room-door", 18L)));

        var event = result.toOptional().orElseThrow();
        assertThat(event.getResult()).isEqualTo(AccessResult.DENIED);
        assertThat(event.getDenialReason()).isEqualTo(DenialReason.OUTSIDE_SCOPE);
    }

    @Test
    void reportsAnUnknownCredential() {
        when(credentialRepository.findById(99L)).thenReturn(Optional.empty());

        var result = service.handle(new CreateAccessEventCommand(new AccessEventData(1L, DURING, 99L, "elevator", null)));

        assertThat(result.isSuccess()).isFalse();
    }
}
