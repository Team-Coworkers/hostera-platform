package com.teamcoworkers.hostera.platform.accesscontrol.interfaces.rest;

import com.teamcoworkers.hostera.platform.accesscontrol.application.commands.CreateCredentialCommand;
import com.teamcoworkers.hostera.platform.accesscontrol.application.commandservices.CredentialCommandService;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queries.GetAllCredentialsQuery;
import com.teamcoworkers.hostera.platform.accesscontrol.application.queryservices.CredentialQueryService;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.teamcoworkers.hostera.platform.accesscontrol.CredentialFixtures.guestCard;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CredentialsControllerTest {

    private static final String GUEST_CARD = """
            {"propertyId": 1, "cardId": "8F2A", "type": "guest-key-card", "holderName": "Elena Ferrer",
             "staffMemberId": null, "bookingId": 3, "bookingCode": "BKG-1032", "roomId": 6, "scope": null,
             "validFrom": "2026-10-03T20:26:00.000Z", "validUntil": "2026-10-07T16:00:00.000Z",
             "issuedAt": "2026-10-03T20:26:00.000Z", "issuedBy": "Demo operator", "revokedAt": null,
             "revokedBy": null, "revocationReason": null, "revocationNote": ""}""";

    @Mock
    private CredentialCommandService commandService;

    @Mock
    private CredentialQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CredentialsController(commandService, queryService)).build();
    }

    @Test
    void listsTheKeyCardsOfABooking() throws Exception {
        when(queryService.handle(new GetAllCredentialsQuery(1L, null, 3L))).thenReturn(List.of(guestCard(1L, 6L, null)));

        mockMvc.perform(get("/api/v1/credentials").param("propertyId", "1").param("bookingId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cardId").value("8F2A"))
                .andExpect(jsonPath("$[0].type").value("guest-key-card"))
                .andExpect(jsonPath("$[0].roomId").value(6))
                .andExpect(jsonPath("$[0].revokedAt").doesNotExist())
                .andExpect(jsonPath("$[0].status").value("expired"));
    }

    @Test
    void issuesAKeyCard() throws Exception {
        when(commandService.handle(any(CreateCredentialCommand.class))).thenReturn(Result.success(guestCard(21L, 6L, null)));

        mockMvc.perform(post("/api/v1/credentials").contentType(MediaType.APPLICATION_JSON).content(GUEST_CARD))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(21));
    }

    @Test
    void answersUnprocessableWhenTheGuestIsNotInHouse() throws Exception {
        when(commandService.handle(any(CreateCredentialCommand.class))).thenReturn(Result.failure(
                ApplicationError.businessRuleViolation("guest-in-house", "Room 6 is not assigned to booking 3")));

        mockMvc.perform(post("/api/v1/credentials").contentType(MediaType.APPLICATION_JSON).content(GUEST_CARD))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void rejectsACredentialWithoutCardId() throws Exception {
        mockMvc.perform(post("/api/v1/credentials").contentType(MediaType.APPLICATION_JSON)
                        .content(GUEST_CARD.replace("\"8F2A\"", "\"\"")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(commandService);
    }
}
