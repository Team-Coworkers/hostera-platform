package com.teamcoworkers.hostera.platform.bookings.application.commands;

import java.math.BigDecimal;
import java.time.Instant;

public record CreatePaymentCommand(Long propertyId, Long bookingId, BigDecimal amount, String method,
                                   Instant paidAt, String reference, String recordedBy) {
}
