package com.teamcoworkers.hostera.platform.accesscontrol.application.commands;

public record UpdateAccessEventCommand(Long accessEventId, AccessEventData data) {
}
