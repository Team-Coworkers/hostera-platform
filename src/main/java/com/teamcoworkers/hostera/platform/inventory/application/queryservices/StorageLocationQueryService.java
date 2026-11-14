package com.teamcoworkers.hostera.platform.inventory.application.queryservices;

import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllStorageLocationsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetStorageLocationByIdQuery;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;

import java.util.List;
import java.util.Optional;

public interface StorageLocationQueryService {
    List<StorageLocation> handle(GetAllStorageLocationsQuery query);
    Optional<StorageLocation> handle(GetStorageLocationByIdQuery query);
}
