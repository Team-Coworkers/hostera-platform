package com.teamcoworkers.hostera.platform.bookings.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.bookings.application.commands.CreatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.DeletePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.PaymentCommandService;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Booking;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.bookings.domain.model.valueobjects.PaymentMethod;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.PaymentRepository;
import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.shared.application.result.ApplicationError;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Records, replaces and deletes payments. A payment must point to an existing booking of the same
 * property.
 */
@Service
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final OrganizationsContextFacade organizationsContextFacade;

    public PaymentCommandServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository,
                                     OrganizationsContextFacade organizationsContextFacade) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.organizationsContextFacade = organizationsContextFacade;
    }

    @Override
    public Result<Payment, ApplicationError> handle(CreatePaymentCommand command) {
        var invalidReference = checkReferences(command.propertyId(), command.bookingId());
        if (invalidReference.isPresent()) return Result.failure(invalidReference.get());
        try {
            return Result.success(paymentRepository.save(new Payment(command.propertyId(), command.bookingId(),
                    command.amount(), PaymentMethod.fromValue(command.method()), command.paidAt(),
                    command.reference(), command.recordedBy())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("payment", e.getMessage()));
        }
    }

    @Override
    public Result<Payment, ApplicationError> handle(UpdatePaymentCommand command) {
        var existing = paymentRepository.findById(command.paymentId());
        if (existing.isEmpty())
            return Result.failure(ApplicationError.notFound("Payment", String.valueOf(command.paymentId())));
        var invalidReference = checkReferences(command.propertyId(), command.bookingId());
        if (invalidReference.isPresent()) return Result.failure(invalidReference.get());
        try {
            var payment = existing.get().updateInformation(command.propertyId(), command.bookingId(),
                    command.amount(), PaymentMethod.fromValue(command.method()), command.paidAt(),
                    command.reference(), command.recordedBy());
            return Result.success(paymentRepository.save(payment));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("payment", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeletePaymentCommand command) {
        if (!paymentRepository.existsById(command.paymentId()))
            return Result.failure(ApplicationError.notFound("Payment", String.valueOf(command.paymentId())));
        paymentRepository.deleteById(command.paymentId());
        return Result.success(command.paymentId());
    }

    /** @return the error when the property or the booking does not exist, or they do not match */
    private Optional<ApplicationError> checkReferences(Long propertyId, Long bookingId) {
        if (!organizationsContextFacade.existsPropertyById(propertyId))
            return Optional.of(ApplicationError.notFound("Property", String.valueOf(propertyId)));
        Optional<Booking> booking = bookingId == null ? Optional.empty() : bookingRepository.findById(bookingId);
        if (booking.isEmpty())
            return Optional.of(ApplicationError.notFound("Booking", String.valueOf(bookingId)));
        if (!booking.get().getPropertyId().equals(propertyId))
            return Optional.of(ApplicationError.validationError("payment",
                    "Booking %d does not belong to property %d".formatted(bookingId, propertyId)));
        return Optional.empty();
    }
}
