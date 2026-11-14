package com.teamcoworkers.hostera.platform.rooms.application.commands;

import java.math.BigDecimal;

public record UpdateDailyRateCommand(Long dailyRateId, Long propertyId, Long ratePlanId, Long roomTypeId, String date, BigDecimal amount) {
}
