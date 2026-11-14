package com.teamcoworkers.hostera.platform.inventory.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.inventory.application.queries.GetAllStorageLocationsQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queries.GetStorageLocationByIdQuery;
import com.teamcoworkers.hostera.platform.inventory.application.queryservices.StorageLocationQueryService;
import com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates.StorageLocation;
import com.teamcoworkers.hostera.platform.inventory.domain.repositories.StorageLocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StorageLocationQueryServiceImpl implements StorageLocationQueryService {
    private final StorageLocationRepository storageLocationRepository;

    public StorageLocationQueryServiceImpl(StorageLocationRepository storageLocationRepository) {
        this.storageLocationRepository = storageLocationRepository;
    }

    @Override
    public List<StorageLocation> handle(GetAllStorageLocationsQuery query) {
        var storageLocations = query.propertyId() == null
                ? storageLocationRepository.findAll()
                : storageLocationRepository.findAllByPropertyId(query.propertyId());
        return storageLocations.stream()
                .filter(storageLocation -> query.type() == null || query.type().equalsIgnoreCase(storageLocation.getType().value()))
                .toList();
    }

    @Override
    public Optional<StorageLocation> handle(GetStorageLocationByIdQuery query) {
        return storageLocationRepository.findById(query.storageLocationId());
    }
}
