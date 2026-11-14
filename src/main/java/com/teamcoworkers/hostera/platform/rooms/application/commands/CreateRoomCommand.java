package com.teamcoworkers.hostera.platform.rooms.application.commands;

public record CreateRoomCommand(Long propertyId, String number, Long roomTypeId, Integer floor, String status) {
}
