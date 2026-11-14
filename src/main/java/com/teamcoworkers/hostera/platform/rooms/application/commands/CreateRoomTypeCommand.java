package com.teamcoworkers.hostera.platform.rooms.application.commands;

import java.math.BigDecimal;

public record CreateRoomTypeCommand(Long propertyId, String name, Integer capacity, String bedConfiguration, BigDecimal baseNightlyRate, String status) {
}
