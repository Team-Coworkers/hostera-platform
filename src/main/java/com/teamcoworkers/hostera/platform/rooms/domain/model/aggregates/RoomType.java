package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Room type aggregate root: a category of rooms of one property (for example, Standard or Suite)
 * that shares capacity, bed configuration and base nightly rate.
 * <p>
 * Invariants: it belongs to a property, its name and bed configuration are not blank, its
 * capacity is between 1 and {@value #MAX_CAPACITY} guests and its base nightly rate is greater
 * than zero.
 * </p>
 */
@Getter
public class RoomType extends AbstractDomainAggregateRoot<RoomType> {
    public static final int MAX_CAPACITY = 12;

    private Long id;
    private Long propertyId;
    private String name;
    private Integer capacity;
    private String bedConfiguration;
    private NightlyRate baseNightlyRate;
    private ActivationStatus status;

    public RoomType(Long propertyId, String name, Integer capacity, String bedConfiguration,
                    NightlyRate baseNightlyRate, ActivationStatus status) {
        updateInformation(propertyId, name, capacity, bedConfiguration, baseNightlyRate, status);
    }

    public static RoomType reconstitute(Long id, Long propertyId, String name, Integer capacity, String bedConfiguration,
                                        NightlyRate baseNightlyRate, ActivationStatus status) {
        var roomType = new RoomType(propertyId, name, capacity, bedConfiguration, baseNightlyRate, status);
        roomType.id = id;
        return roomType;
    }

    public RoomType updateInformation(Long propertyId, String name, Integer capacity, String bedConfiguration,
                                      NightlyRate baseNightlyRate, ActivationStatus status) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Room type name is required");
        if (capacity == null || capacity < 1 || capacity > MAX_CAPACITY)
            throw new IllegalArgumentException("Capacity must be between 1 and %d guests".formatted(MAX_CAPACITY));
        if (bedConfiguration == null || bedConfiguration.isBlank())
            throw new IllegalArgumentException("Bed configuration is required");
        if (baseNightlyRate == null) throw new IllegalArgumentException("Base nightly rate is required");
        if (status == null) throw new IllegalArgumentException("Room type status is required");
        this.propertyId = propertyId;
        this.name = name.trim();
        this.capacity = capacity;
        this.bedConfiguration = bedConfiguration.trim();
        this.baseNightlyRate = baseNightlyRate;
        this.status = status;
        return this;
    }

    public boolean isActive() {
        return status == ActivationStatus.ACTIVE;
    }
}
