package com.teamcoworkers.hostera.platform.inventory.application.commands;

public record CreateStorageLocationCommand(Long propertyId, String name, String code, String type, String area, String responsibleTeam, String description) {
}
