package com.teamcoworkers.hostera.platform.inventory.application.commands;

public record UpdateStorageLocationCommand(Long storageLocationId, Long propertyId, String name, String code, String type, String area, String responsibleTeam, String description) {
}
