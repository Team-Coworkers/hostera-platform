package com.teamcoworkers.hostera.platform.bookings.application.commandservices;

import com.teamcoworkers.hostera.platform.bookings.application.commands.CreateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.DeleteBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface BookingCommandService {
    Result<Booking, ApplicationError> handle(CreateBookingCommand command);

    Result<Booking, ApplicationError> handle(UpdateBookingCommand command);

    Result<Long, ApplicationError> handle(DeleteBookingCommand command);
}
