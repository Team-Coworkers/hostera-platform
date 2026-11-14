package com.teamcoworkers.hostera.platform.bookings.interfaces.rest.transform;

import com.teamcoworkers.hostera.platform.bookings.application.commands.CreatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.UpdatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.domain.model.aggregates.Payment;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.PaymentResource;
import com.teamcoworkers.hostera.platform.bookings.interfaces.rest.resources.SavePaymentResource;

public final class PaymentResourceFromEntityAssembler {

    private PaymentResourceFromEntityAssembler() {
    }

    public static PaymentResource toResourceFromEntity(Payment payment) {
        return new PaymentResource(payment.getId(), payment.getPropertyId(), payment.getBookingId(),
                payment.getAmount(), payment.getMethod().value(), payment.getPaidAt(), payment.getReference(),
                payment.getRecordedBy());
    }

    public static CreatePaymentCommand toCreateCommand(SavePaymentResource resource) {
        return new CreatePaymentCommand(resource.propertyId(), resource.bookingId(), resource.amount(),
                resource.method(), resource.paidAt(), resource.reference(), resource.recordedBy());
    }

    public static UpdatePaymentCommand toUpdateCommand(Long paymentId, SavePaymentResource resource) {
        return new UpdatePaymentCommand(paymentId, resource.propertyId(), resource.bookingId(), resource.amount(),
                resource.method(), resource.paidAt(), resource.reference(), resource.recordedBy());
    }
}
