package com.teamcoworkers.hostera.platform.rooms.application.commands;

import java.util.List;

public record CreateRatePlanCommand(Long propertyId, String name, List<Long> roomTypeIds, String includedServices, Boolean refundable, String cancellationPolicy, String status) {
}
