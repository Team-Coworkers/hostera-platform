package com.teamcoworkers.hostera.platform.bookings.application.commands;

public record UpdateBookingCommand(Long bookingId, BookingData data) {
}
