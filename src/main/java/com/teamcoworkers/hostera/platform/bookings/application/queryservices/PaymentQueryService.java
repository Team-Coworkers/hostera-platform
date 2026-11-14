package com.teamcoworkers.hostera.platform.bookings.application.queryservices;

import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllPaymentsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetPaymentByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentQueryService {
    List<Payment> handle(GetAllPaymentsQuery query);

    Optional<Payment> handle(GetPaymentByIdQuery query);
}
