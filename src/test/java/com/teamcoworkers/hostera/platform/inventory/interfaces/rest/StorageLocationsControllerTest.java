package com.teamcoworkers.hostera.platform.inventory.interfaces.rest;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commandservices.StorageLocationCommandService;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllStorageLocationsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queryservices.StorageLocationQueryService;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StorageLocationsControllerTest {
    private static final String HOUSEKEEPING = "{\"propertyId\":1,\"name\":\"Housekeeping\",\"code\":\"HK-01\",\"type\":\"warehouse\","
            + "\"area\":\"Ground floor\",\"responsibleTeam\":\"Housekeeping\",\"description\":\"Linen and guest supplies.\"}";

    @Mock
    private StorageLocationCommandService commandService;

    @Mock
    private StorageLocationQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StorageLocationsController(commandService, queryService)).build();
    }

    @Test
    void listsTheStorageLocationsOfAProperty() throws Exception {
        when(queryService.handle(new GetAllStorageLocationsQuery(1L, null))).thenReturn(List.of(
                StorageLocation.reconstitute(1L, 1L, "Housekeeping", "HK-01", StorageLocationType.WAREHOUSE, "Ground floor",
                        "Housekeeping", "Linen and guest supplies.")));

        mockMvc.perform(get("/api/v1/storage-locations").param("propertyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("HK-01"))
                .andExpect(jsonPath("$[0].type").value("warehouse"))
                .andExpect(jsonPath("$[0].responsibleTeam").value("Housekeeping"));
    }

    @Test
    void answersConflictWhenTheCodeAlreadyExistsInTheProperty() throws Exception {
        when(commandService.handle(any(CreateStorageLocationCommand.class)))
                .thenReturn(Result.failure(ApplicationError.conflict("Storage_Location", "Code 'HK-01' already exists in the property")));

        mockMvc.perform(post("/api/v1/storage-locations").contentType(MediaType.APPLICATION_JSON).content(HOUSEKEEPING))
                .andExpect(status().isConflict());
    }

    @Test
    void answersUnprocessableWhenTheLocationIsStillInUse() throws Exception {
        when(commandService.handle(any(DeleteStorageLocationCommand.class)))
                .thenReturn(Result.failure(ApplicationError.businessRuleViolation("storage-location-in-use",
                        "Move the stock and the primary location of its items to another storage location first")));

        mockMvc.perform(delete("/api/v1/storage-locations/1")).andExpect(status().is(422));
    }
}
