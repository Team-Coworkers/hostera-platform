package com.teamcoworkers.hostera.platform.rooms.application.commands;

public record UpdateRoomCommand(Long roomId, Long propertyId, String number, Long roomTypeId, Integer floor, String status) {
}
