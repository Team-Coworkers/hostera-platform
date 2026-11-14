package com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates;

import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.IncludedServices;
import com.teamcoworkers.hostera.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.List;

/**
 * Rate plan aggregate root: a set of commercial conditions (included services and cancellation
 * terms) under which a property sells its room types.
 * <p>
 * Invariants: it belongs to a property, its name is not blank, a refundable plan states its
 * cancellation policy and a non-refundable plan has none, and it lists each room type at most
 * once. An empty list of room types means that it applies to all of them.
 * </p>
 */
@Getter
public class RatePlan extends AbstractDomainAggregateRoot<RatePlan> {
    private Long id;
    private Long propertyId;
    private String name;
    private List<Long> roomTypeIds;
    private IncludedServices includedServices;
    private boolean refundable;
    private String cancellationPolicy;
    private ActivationStatus status;

    public RatePlan(Long propertyId, String name, List<Long> roomTypeIds, IncludedServices includedServices,
                    Boolean refundable, String cancellationPolicy, ActivationStatus status) {
        updateInformation(propertyId, name, roomTypeIds, includedServices, refundable, cancellationPolicy, status);
    }

    public static RatePlan reconstitute(Long id, Long propertyId, String name, List<Long> roomTypeIds,
                                        IncludedServices includedServices, Boolean refundable,
                                        String cancellationPolicy, ActivationStatus status) {
        var ratePlan = new RatePlan(propertyId, name, roomTypeIds, includedServices, refundable, cancellationPolicy, status);
        ratePlan.id = id;
        return ratePlan;
    }

    public RatePlan updateInformation(Long propertyId, String name, List<Long> roomTypeIds, IncludedServices includedServices,
                                      Boolean refundable, String cancellationPolicy, ActivationStatus status) {
        if (propertyId == null || propertyId <= 0)
            throw new IllegalArgumentException("Property id must be a positive number");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Rate plan name is required");
        if (includedServices == null) throw new IllegalArgumentException("Included services are required");
        if (refundable == null) throw new IllegalArgumentException("Refundable is required");
        if (status == null) throw new IllegalArgumentException("Rate plan status is required");
        var roomTypes = roomTypeIds == null ? List.<Long>of() : roomTypeIds;
        if (roomTypes.stream().anyMatch(roomTypeId -> roomTypeId == null || roomTypeId <= 0))
            throw new IllegalArgumentException("Room type ids must be positive numbers");
        if (roomTypes.stream().distinct().count() != roomTypes.size())
            throw new IllegalArgumentException("A rate plan lists each room type only once");
        var policy = cancellationPolicy == null ? "" : cancellationPolicy.trim();
        if (refundable && policy.isEmpty())
            throw new IllegalArgumentException("A refundable rate plan needs a cancellation policy");
        this.propertyId = propertyId;
        this.name = name.trim();
        this.roomTypeIds = List.copyOf(roomTypes);
        this.includedServices = includedServices;
        this.refundable = refundable;
        this.cancellationPolicy = refundable ? policy : "";
        this.status = status;
        return this;
    }

    public boolean isActive() {
        return status == ActivationStatus.ACTIVE;
    }

    /** @return whether the plan can price the given room type */
    public boolean appliesTo(Long roomTypeId) {
        return roomTypeIds.isEmpty() || roomTypeIds.contains(roomTypeId);
    }
}
