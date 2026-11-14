package com.teamcoworkers.hostera.platform.bookings.application.commandservices;

import com.teamcoworkers.hostera.platform.bookings.application.commands.CreatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.DeletePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;

public interface PaymentCommandService {
    Result<Payment, ApplicationError> handle(CreatePaymentCommand command);

    Result<Payment, ApplicationError> handle(UpdatePaymentCommand command);

    Result<Long, ApplicationError> handle(DeletePaymentCommand command);
}
