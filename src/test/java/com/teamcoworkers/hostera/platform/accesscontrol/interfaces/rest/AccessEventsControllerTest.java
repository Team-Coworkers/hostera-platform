package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.AccessEventCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllAccessEventsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices.AccessEventQueryService;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.aggregates.AccessEvent;
import com.teamcoworkers.hostera.platform.accesscontrol.domain.model.valueobjects.AccessPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.DURING;
import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.guestCard;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccessEventsControllerTest {

    @Mock
    private AccessEventCommandService commandService;

    @Mock
    private AccessEventQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AccessEventsController(commandService, queryService)).build();
    }

    @Test
    void listsTheAttemptsOfARoom() throws Exception {
        var event = AccessEvent.recordAttempt(guestCard(1L, 6L, null), AccessPoint.ROOM_DOOR, 6L, DURING);
        when(queryService.handle(new GetAllAccessEventsQuery(1L, 6L, null, null))).thenReturn(List.of(event));

        mockMvc.perform(get("/api/v1/access-events").param("propertyId", "1").param("roomId", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accessPoint").value("room-door"))
                .andExpect(jsonPath("$[0].holderType").value("guest"))
                .andExpect(jsonPath("$[0].result").value("granted"))
                .andExpect(jsonPath("$[0].denialReason").doesNotExist());
    }
}
