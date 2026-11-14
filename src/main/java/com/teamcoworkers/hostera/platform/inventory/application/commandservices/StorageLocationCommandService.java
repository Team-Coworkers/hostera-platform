package com.teamcoworkers.hostera.platform.inventory.application.commandservices;

import com.teamcoworkers.hostera.platform.inventory.application.commands.CreateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.DeleteStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.application.commands.UpdateStorageLocationCommand;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface StorageLocationCommandService {
    Result<StorageLocation, ApplicationError> handle(CreateStorageLocationCommand command);
    Result<StorageLocation, ApplicationError> handle(UpdateStorageLocationCommand command);
    Result<Long, ApplicationError> handle(DeleteStorageLocationCommand command);
}
