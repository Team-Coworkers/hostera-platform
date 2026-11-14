package com.teamcoworkers.hostera.platform.inventory.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.entities.StockAdjustment;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.InventoryUnit;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.Stock;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StockOperation;
import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.InventoryItemRepository;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.StorageLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Loads the demonstration storage locations and inventory items of the Hostera frontend on an
 * empty database, so the deployed application does not start empty. The rows keep the order of
 * {@code server/data} of the frontend, so their generated identifiers match the ones that the
 * items reference. It runs after the organizations seed.
 */
@Service
public class InventorySeedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InventorySeedEventHandler.class);
    private static final Instant OPENING_DAY = Instant.parse("2026-10-04T10:00:00Z");

    private final StorageLocationRepository storageLocationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final boolean seedEnabled;

    public InventorySeedEventHandler(StorageLocationRepository storageLocationRepository,
                                     InventoryItemRepository inventoryItemRepository,
                                     @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.storageLocationRepository = storageLocationRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.seedEnabled = seedEnabled;
    }

    @Order(30)
    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedEnabled || storageLocationRepository.count() > 0) return;
        try {
            seedStorageLocations();
            seedInventoryItems();
            LOGGER.info("Seeded {} storage locations and {} inventory items", storageLocationRepository.count(), inventoryItemRepository.count());
        } catch (RuntimeException e) {
            LOGGER.warn("The inventory seed stopped: {}", e.getMessage());
        }
    }

    private void seedStorageLocations() {
        // propertyId, name, code, type, area, responsibleTeam, description
        String[][] storageLocations = {
                {"1", "Housekeeping", "HK-01", "warehouse", "Ground floor", "Housekeeping", "Linen and guest supplies."},
                {"1", "Main storage", "MAIN-01", "warehouse", "Basement", "Operations", "Central supplies."},
                {"1", "Floor 3 closet", "F3-01", "closet", "Floor 3", "Housekeeping", "Floor service supplies."},
                {"1", "Front desk", "FD-01", "front-desk", "Lobby", "Front desk", "Access supplies."},
                {"2", "Main storage", "MAIN-01", "warehouse", "Ground floor", "Operations", ""},
        };
        for (var row : storageLocations)
            storageLocationRepository.save(new StorageLocation(Long.valueOf(row[0]), row[1], row[2],
                    StorageLocationType.fromValue(row[3]), row[4], row[5], row[6]));
    }

    private void seedInventoryItems() {
        inventoryItemRepository.save(new InventoryItem(1L, "Bath towels", "LIN-BT-001", "linen",
                InventoryUnit.UNITS, 1L, new BigDecimal("30"),
                List.of(stock(1L, "60"), stock(3L, "24")),
                List.of(opening("opening-1-1", "60", 1L, "Housekeeping"), opening("opening-1-3", "24", 3L, "Floor 3 closet"))));
        inventoryItemRepository.save(new InventoryItem(1L, "Queen sheet sets", "LIN-QN-002", "linen",
                InventoryUnit.UNITS, 1L, new BigDecimal("20"),
                List.of(stock(1L, "10"), stock(3L, "8")),
                List.of(opening("opening-2-1", "10", 1L, "Housekeeping"), opening("opening-2-3", "8", 3L, "Floor 3 closet"))));
        inventoryItemRepository.save(new InventoryItem(1L, "Shampoo bottles", "AM-SH-003", "guest-amenities",
                InventoryUnit.UNITS, 2L, new BigDecimal("40"),
                List.of(stock(2L, "120")),
                List.of(opening("opening-3-2", "120", 2L, "Main storage"))));
        inventoryItemRepository.save(new InventoryItem(1L, "RFID key cards", "AC-KC-004", "access-supplies",
                InventoryUnit.UNITS, 4L, new BigDecimal("20"),
                List.of(stock(4L, "12")),
                List.of(opening("opening-4-4", "12", 4L, "Front desk"))));
        inventoryItemRepository.save(new InventoryItem(1L, "All-purpose cleaner", "CL-AP-005", "cleaning-supplies",
                InventoryUnit.LITERS, 2L, new BigDecimal("5"),
                List.of(stock(2L, "0")),
                List.of()));
        inventoryItemRepository.save(new InventoryItem(1L, "Laundry detergent", "CL-LD-006", "cleaning-supplies",
                InventoryUnit.KILOGRAMS, 1L, new BigDecimal("8"),
                List.of(stock(1L, "12.5")),
                List.of(opening("opening-6-1", "12.5", 1L, "Housekeeping"))));
        inventoryItemRepository.save(new InventoryItem(2L, "Bath towels", "LIN-BT-001", "linen",
                InventoryUnit.UNITS, 5L, new BigDecimal("10"),
                List.of(stock(5L, "32")),
                List.of(opening("opening-7-5", "32", 5L, "Main storage"))));
        inventoryItemRepository.save(new InventoryItem(2L, "Hand soap", "AM-HS-002", "guest-amenities",
                InventoryUnit.LITERS, 5L, new BigDecimal("5"),
                List.of(stock(5L, "3.5")),
                List.of(opening("opening-8-5", "3.5", 5L, "Main storage"))));
    }

    private static Stock stock(Long locationId, String quantity) {
        return new Stock(locationId, new BigDecimal(quantity));
    }

    /** Opening stock recorded when the property started to use Hostera. */
    private static StockAdjustment opening(String id, String quantity, Long locationId, String locationName) {
        return new StockAdjustment(id, StockOperation.IN, new BigDecimal(quantity), locationId, locationName,
                "opening-stock", "", OPENING_DAY, "Demo operator", null);
    }
}
