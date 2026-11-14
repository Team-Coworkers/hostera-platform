package com.teamcoworkers.hostera.platform.inventory.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.inventory.domain.model.valueobjects.StorageLocationType;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Storage location aggregate root: a place of a property (warehouse, closet, front desk or service
 * area) where supplies are kept.
 * <p>
 * Invariants: it belongs to a property and has a name, a code (unique in the property, which the
 * application layer checks), a type, an area and a responsible team.
 * </p>
 */
@Getter
public class StorageLocation extends AbstractDomainAggregateRoot<StorageLocation> {
    private Long id;
    private Long propertyId;
    private String name;
    private String code;
    private StorageLocationType type;
    private String area;
    private String responsibleTeam;
    private String description;

    public StorageLocation(Long propertyId, String name, String code, StorageLocationType type, String area,
                           String responsibleTeam, String description) {
        updateInformation(propertyId, name, code, type, area, responsibleTeam, description);
    }

    public static StorageLocation reconstitute(Long id, Long propertyId, String name, String code, StorageLocationType type,
                                               String area, String responsibleTeam, String description) {
        var storageLocation = new StorageLocation(propertyId, name, code, type, area, responsibleTeam, description);
        storageLocation.id = id;
        return storageLocation;
    }

    public StorageLocation updateInformation(Long propertyId, String name, String code, StorageLocationType type, String area,
                                             String responsibleTeam, String description) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Storage location name is required");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Storage location code is required");
        if (type == null) throw new IllegalArgumentException("Storage location type is required");
        if (area == null || area.isBlank()) throw new IllegalArgumentException("Storage location area is required");
        if (responsibleTeam == null || responsibleTeam.isBlank())
            throw new IllegalArgumentException("Responsible team is required");
        this.propertyId = propertyId;
        this.name = name.trim();
        this.code = code.trim();
        this.type = type;
        this.area = area.trim();
        this.responsibleTeam = responsibleTeam.trim();
        this.description = description == null ? "" : description.trim();
        return this;
    }
}
