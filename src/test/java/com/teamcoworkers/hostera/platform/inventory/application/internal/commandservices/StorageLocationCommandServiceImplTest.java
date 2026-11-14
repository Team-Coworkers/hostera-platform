package com.teamcoworkers.hostera.platform.inventory.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageLocationCommandServiceImplTest {

    @Mock
    private StorageLocationRepository storageLocationRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @InjectMocks
    private StorageLocationCommandServiceImpl service;

    private static StorageLocation housekeeping() {
        return StorageLocation.reconstitute(1L, 1L, "Housekeeping", "HK-01", StorageLocationType.WAREHOUSE, "Ground floor",
                "Housekeeping", "Linen and guest supplies.");
    }

    private static InventoryItem bathTowelsWith(String quantityAtHousekeeping, Long primaryLocationId) {
        return InventoryItem.reconstitute(1L, 1L, "Bath towels", "LIN-BT-001", "linen", InventoryUnit.UNITS, primaryLocationId,
                new BigDecimal("30"), List.of(new Stock(1L, new BigDecimal(quantityAtHousekeeping))), List.of());
    }

    @Test
    void createsTheStorageLocationWhenTheCodeIsNew() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(storageLocationRepository.existsByPropertyIdAndCode(1L, "HK-01")).thenReturn(false);
        when(storageLocationRepository.save(any(StorageLocation.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateStorageLocationCommand(1L, "Housekeeping", "HK-01", "warehouse", "Ground floor", "Housekeeping", ""));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getType()).isEqualTo(StorageLocationType.WAREHOUSE);
    }

    @Test
    void rejectsACodeThatAlreadyExistsInTheProperty() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(storageLocationRepository.existsByPropertyIdAndCode(1L, "HK-01")).thenReturn(true);

        var result = service.handle(new CreateStorageLocationCommand(1L, "Housekeeping", "HK-01", "warehouse", "Ground floor", "Housekeeping", ""));

        assertThat(result.isFailure()).isTrue();
        verify(storageLocationRepository, never()).save(any());
    }

    @Test
    void doesNotDeleteALocationThatStillHoldsStock() {
        when(storageLocationRepository.findById(1L)).thenReturn(Optional.of(housekeeping()));
        when(inventoryItemRepository.findAllByPropertyId(1L)).thenReturn(List.of(bathTowelsWith("60", 2L)));

        var result = service.handle(new DeleteStorageLocationCommand(1L));

        assertThat(result.isFailure()).isTrue();
        verify(storageLocationRepository, never()).deleteById(anyLong());
    }

    @Test
    void deletesALocationThatNoItemUses() {
        when(storageLocationRepository.findById(1L)).thenReturn(Optional.of(housekeeping()));
        when(inventoryItemRepository.findAllByPropertyId(1L)).thenReturn(List.of(bathTowelsWith("0", 2L)));

        var result = service.handle(new DeleteStorageLocationCommand(1L));

        assertThat(result.isSuccess()).isTrue();
        verify(storageLocationRepository).deleteById(1L);
    }
}
