package com.teamcoworkers.hostera.platform.rooms.application.commands;

public record UpdateStatusPeriodCommand(Long statusPeriodId, Long propertyId, Long roomId, String status, String startDate, String endDate, String reason) {
}
