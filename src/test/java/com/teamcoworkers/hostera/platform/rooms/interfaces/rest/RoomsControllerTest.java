package com.teamcoworkers.hostera.platform.rooms.interfaces.rest;

import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commands.UpdateRoomCommand;
import com.teamcoworkers.hostera.platform.rooms.application.commandservices.RoomCommandService;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRoomsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRoomByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RoomQueryService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.RoomStatus;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoomsControllerTest {

    @Mock
    private RoomCommandService commandService;

    @Mock
    private RoomQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RoomsController(commandService, queryService)).build();
    }

    @Test
    void listsTheRoomsOfAPropertyWithTheContractOfTheFrontend() throws Exception {
        when(queryService.handle(new GetAllRoomsQuery(1L, null, null)))
                .thenReturn(List.of(Room.reconstitute(1L, 1L, "201", 1L, 2, RoomStatus.READY)));

        mockMvc.perform(get("/api/v1/rooms").param("propertyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].propertyId").value(1))
                .andExpect(jsonPath("$[0].number").value("201"))
                .andExpect(jsonPath("$[0].roomTypeId").value(1))
                .andExpect(jsonPath("$[0].floor").value(2))
                .andExpect(jsonPath("$[0].status").value("ready"));
    }

    @Test
    void returnsNotFoundWhenTheRoomDoesNotExist() throws Exception {
        when(queryService.handle(new GetRoomByIdQuery(99L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/rooms/99")).andExpect(status().isNotFound());
    }

    @Test
    void createsARoom() throws Exception {
        when(commandService.handle(any(CreateRoomCommand.class)))
                .thenReturn(Result.success(Room.reconstitute(23L, 1L, "305", 3L, 3, RoomStatus.AVAILABLE)));

        mockMvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":null,\"propertyId\":1,\"number\":\"305\",\"roomTypeId\":3,\"floor\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(23))
                .andExpect(jsonPath("$.number").value("305"))
                .andExpect(jsonPath("$.status").value("available"));
    }

    @Test
    void rejectsARoomWithoutNumber() throws Exception {
        mockMvc.perform(post("/api/v1/rooms").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"propertyId\":1,\"number\":\"\",\"roomTypeId\":3,\"floor\":3}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commandService);
    }

    @Test
    void answersUnprocessableWhenTheStatusTransitionIsNotAllowed() throws Exception {
        when(commandService.handle(any(UpdateRoomCommand.class)))
                .thenReturn(Result.failure(ApplicationError.businessRuleViolation("room-status-transition",
                        "A room that is occupied cannot change to ready")));

        mockMvc.perform(put("/api/v1/rooms/7").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":7,\"propertyId\":1,\"number\":\"201\",\"roomTypeId\":1,\"floor\":2,\"status\":\"ready\"}"))
                .andExpect(status().is(422));
    }
}
