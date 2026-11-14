package com.teamcoworkers.hostera.platform.inventory.application.commandservices;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateInventoryItemCommand;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.InventoryItem;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface InventoryItemCommandService {
    Result<InventoryItem, ApplicationError> handle(CreateInventoryItemCommand command);
    Result<InventoryItem, ApplicationError> handle(UpdateInventoryItemCommand command);
    Result<Long, ApplicationError> handle(DeleteInventoryItemCommand command);
}
