package com.teamcoworkers.hostera.platform.inventory.application.queryservices;

import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllInventoryItemsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetInventoryItemByIdQuery;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;

import java.util.List;
import java.util.Optional;

public interface InventoryItemQueryService {
    List<InventoryItem> handle(GetAllInventoryItemsQuery query);
    Optional<InventoryItem> handle(GetInventoryItemByIdQuery query);
}
