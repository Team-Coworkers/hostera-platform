package com.teamcoworkers.hostera.platform.organizations.application.commands;

public record UpdateStaffMemberCommand(Long staffMemberId, Long propertyId, String name, String role) {
}
