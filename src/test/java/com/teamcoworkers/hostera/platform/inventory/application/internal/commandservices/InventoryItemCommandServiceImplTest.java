package com.teamcoworkers.hostera.platform.inventory.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.StockAdjustmentItem;
import com.teamcoworkers.hostera.platform.inventory.application.commands.StockItem;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.InventoryItemRepository;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.StorageLocationRepository;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryItemCommandServiceImplTest {
    private static final StockAdjustmentItem OPENING = new StockAdjustmentItem("opening-1-1", "in", new BigDecimal("60"), 1L,
            "Housekeeping", "opening-stock", "", "2026-10-04T10:00:00Z", "Demo operator", null);

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private StorageLocationRepository storageLocationRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @InjectMocks
    private InventoryItemCommandServiceImpl service;

    private static StorageLocation housekeeping() {
        return StorageLocation.reconstitute(1L, 1L, "Housekeeping", "HK-01", StorageLocationType.WAREHOUSE, "Ground floor",
                "Housekeeping", "");
    }

    private static CreateInventoryItemCommand createBathTowels(Long primaryLocationId) {
        return new CreateInventoryItemCommand(1L, "Bath towels", "LIN-BT-001", "linen", "units", primaryLocationId,
                new BigDecimal("30"), List.of(new StockItem(1L, new BigDecimal("60"))), List.of(OPENING));
    }

    @Test
    void createsTheItemWithItsStockAndItsOpeningAdjustment() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(inventoryItemRepository.existsByPropertyIdAndCode(1L, "LIN-BT-001")).thenReturn(false);
        when(storageLocationRepository.findAllByPropertyId(1L)).thenReturn(List.of(housekeeping()));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(createBathTowels(1L));

        assertThat(result.isSuccess()).isTrue();
        var item = result.toOptional().orElseThrow();
        assertThat(item.totalQuantity()).isEqualByComparingTo("60");
        assertThat(item.getAdjustments()).hasSize(1);
    }

    @Test
    void rejectsACodeThatAlreadyExistsInTheProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(inventoryItemRepository.existsByPropertyIdAndCode(1L, "LIN-BT-001")).thenReturn(true);

        var result = service.handle(createBathTowels(1L));

        assertThat(result.isFailure()).isTrue();
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void rejectsAPrimaryLocationThatIsNotOfTheProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(inventoryItemRepository.existsByPropertyIdAndCode(1L, "LIN-BT-001")).thenReturn(false);
        when(storageLocationRepository.findAllByPropertyId(1L)).thenReturn(List.of(housekeeping()));

        var result = service.handle(createBathTowels(5L));

        assertThat(result.isFailure()).isTrue();
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void doesNotLetAnUpdateRemoveARecordedAdjustment() {
        var recorded = new StockAdjustment("opening-1-1", StockOperation.IN, new BigDecimal("60"), 1L, "Housekeeping",
                "opening-stock", "", Instant.parse("2026-10-04T10:00:00Z"), "Demo operator", null);
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(InventoryItem.reconstitute(1L, 1L, "Bath towels",
                "LIN-BT-001", "linen", InventoryUnit.UNITS, 1L, new BigDecimal("30"),
                List.of(new Stock(1L, new BigDecimal("60"))), List.of(recorded))));
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(inventoryItemRepository.existsByPropertyIdAndCodeAndIdIsNot(1L, "LIN-BT-001", 1L)).thenReturn(false);

        var result = service.handle(new UpdateInventoryItemCommand(1L, 1L, "Bath towels", "LIN-BT-001", "linen", "units", 1L,
                new BigDecimal("30"), List.of(new StockItem(1L, new BigDecimal("60"))), List.of()));

        assertThat(result.isFailure()).isTrue();
        verify(inventoryItemRepository, never()).save(any());
    }
}
