package com.teamcoworkers.hostera.platform.inventory.interfaces.rest;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commandservices.InventoryItemCommandService;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllInventoryItemsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queryservices.InventoryItemQueryService;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InventoryItemsControllerTest {

    @Mock
    private InventoryItemCommandService commandService;

    @Mock
    private InventoryItemQueryService queryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryItemsController(commandService, queryService)).build();
    }

    private static InventoryItem bathTowels() {
        var opening = new StockAdjustment("opening-1-1", StockOperation.IN, new BigDecimal("60.000"), 1L, "Housekeeping",
                "opening-stock", "", Instant.parse("2026-10-04T10:00:00Z"), "Demo operator", null);
        return InventoryItem.reconstitute(1L, 1L, "Bath towels", "LIN-BT-001", "linen", InventoryUnit.UNITS, 1L,
                new BigDecimal("30.000"), List.of(new Stock(1L, new BigDecimal("60.000"))), List.of(opening));
    }

    @Test
    void listsTheItemsWithTheirStocksAndAdjustmentsAsTheFrontendReadsThem() throws Exception {
        when(queryService.handle(new GetAllInventoryItemsQuery(1L, null))).thenReturn(List.of(bathTowels()));

        mockMvc.perform(get("/api/v1/inventory-items").param("propertyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("LIN-BT-001"))
                .andExpect(jsonPath("$[0].unit").value("units"))
                .andExpect(jsonPath("$[0].primaryLocationId").value(1))
                .andExpect(jsonPath("$[0].lowStockThreshold").value(30))
                .andExpect(jsonPath("$[0].stocks[0].locationId").value(1))
                .andExpect(jsonPath("$[0].stocks[0].quantity").value(60))
                .andExpect(jsonPath("$[0].adjustments[0].id").value("opening-1-1"))
                .andExpect(jsonPath("$[0].adjustments[0].operation").value("in"))
                .andExpect(jsonPath("$[0].adjustments[0].recordedAt").value("2026-10-04T10:00:00Z"));
    }

    @Test
    void createsAnItemFromThePayloadOfTheFrontend() throws Exception {
        when(commandService.handle(any(CreateInventoryItemCommand.class))).thenReturn(Result.success(bathTowels()));

        mockMvc.perform(post("/api/v1/inventory-items").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":null,\"propertyId\":1,\"name\":\"Bath towels\",\"code\":\"LIN-BT-001\","
                                + "\"category\":\"linen\",\"unit\":\"units\",\"primaryLocationId\":1,\"lowStockThreshold\":30,"
                                + "\"stocks\":[{\"locationId\":1,\"quantity\":60}],"
                                + "\"adjustments\":[{\"id\":\"opening-1-1\",\"operation\":\"in\",\"quantity\":60,\"locationId\":1,"
                                + "\"locationName\":\"Housekeeping\",\"reason\":\"opening-stock\",\"note\":\"\","
                                + "\"recordedAt\":\"2026-10-04T10:00:00Z\",\"operator\":\"Demo operator\",\"transferId\":null}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stocks[0].quantity").value(60));
    }

    @Test
    void rejectsAnItemWithoutCode() throws Exception {
        mockMvc.perform(post("/api/v1/inventory-items").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"propertyId\":1,\"name\":\"Bath towels\",\"code\":\"\",\"category\":\"linen\","
                                + "\"unit\":\"units\",\"primaryLocationId\":1,\"lowStockThreshold\":30}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commandService);
    }
}
