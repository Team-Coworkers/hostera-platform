package com.teamcoworkers.hostera.platform.rooms.application.commands;

public record CreateStatusPeriodCommand(Long propertyId, Long roomId, String status, String startDate, String endDate, String reason) {
}
