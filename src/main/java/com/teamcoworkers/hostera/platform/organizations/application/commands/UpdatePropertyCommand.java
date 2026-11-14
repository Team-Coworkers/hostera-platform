package com.teamcoworkers.hostera.platform.organizations.application.commands;

public record UpdatePropertyCommand(Long propertyId, Long organizationId, String name, String city, String currency) {
}
