package com.teamcoworkers.hostera.platform.bookings.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.bookings.application.queries.GetAllPaymentsQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queries.GetPaymentByIdQuery;
import com.teamcoworkers.hostera.platform.bookings.application.queryservices.PaymentQueryService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public PaymentQueryServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> handle(GetAllPaymentsQuery query) {
        var payments = query.bookingId() != null
                ? paymentRepository.findAllByBookingId(query.bookingId())
                : query.propertyId() != null ? paymentRepository.findAllByPropertyId(query.propertyId())
                : paymentRepository.findAll();
        return payments.stream()
                .filter(payment -> query.propertyId() == null || query.propertyId().equals(payment.getPropertyId()))
                .toList();
    }

    @Override
    public Optional<Payment> handle(GetPaymentByIdQuery query) {
        return paymentRepository.findById(query.paymentId());
    }
}
