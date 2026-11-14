package com.teamcoworkers.hostera.platform.inventory.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllInventoryItemsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetInventoryItemByIdQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queryservices.InventoryItemQueryService;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.InventoryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryItemQueryServiceImpl implements InventoryItemQueryService {
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemQueryServiceImpl(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Override
    public List<InventoryItem> handle(GetAllInventoryItemsQuery query) {
        var inventoryItems = query.propertyId() == null
                ? inventoryItemRepository.findAll()
                : inventoryItemRepository.findAllByPropertyId(query.propertyId());
        return inventoryItems.stream()
                .filter(inventoryItem -> query.category() == null || query.category().equalsIgnoreCase(inventoryItem.getCategory()))
                .toList();
    }

    @Override
    public Optional<InventoryItem> handle(GetInventoryItemByIdQuery query) {
        return inventoryItemRepository.findById(query.inventoryItemId());
    }
}
