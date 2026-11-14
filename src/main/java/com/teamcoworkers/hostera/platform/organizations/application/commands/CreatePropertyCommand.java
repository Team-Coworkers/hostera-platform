package com.teamcoworkers.hostera.platform.organizations.application.commands;

public record CreatePropertyCommand(Long organizationId, String name, String city, String currency) {
}
