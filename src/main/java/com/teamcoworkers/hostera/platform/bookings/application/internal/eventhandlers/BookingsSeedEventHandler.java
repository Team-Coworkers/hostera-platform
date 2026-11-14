package com.teamcoworkers.hostera.platform.bookings.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.bookings.application.commands.BookingData;
import com.teamcoworkers.hostera.platform.bookings.application.commands.CreateBookingCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commands.CreatePaymentCommand;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.BookingCommandService;
import com.teamcoworkers.hostera.platform.bookings.application.commandservices.PaymentCommandService;
import com.teamcoworkers.hostera.platform.bookings.domain.repositories.BookingRepository;
import com.teamcoworkers.hostera.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Loads the demonstration bookings and payments of the Hostera frontend on an empty database. The
 * rows come from {@code seed/bookings.json} and {@code seed/payments.json}, copies of the frontend
 * {@code server/db.json} fixtures, and are saved in the same order so their generated identifiers
 * match the ones the frontend and the access control context reference. It runs after the
 * organizations seed, because every booking belongs to one of its properties.
 */
@Service
public class BookingsSeedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingsSeedEventHandler.class);

    private record BookingsFile(List<BookingData> bookings) {
    }

    private record PaymentsFile(List<CreatePaymentCommand> payments) {
    }

    private final BookingRepository bookingRepository;
    private final BookingCommandService bookingCommandService;
    private final PaymentCommandService paymentCommandService;
    private final boolean seedEnabled;
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public BookingsSeedEventHandler(BookingRepository bookingRepository,
                                    BookingCommandService bookingCommandService,
                                    PaymentCommandService paymentCommandService,
                                    @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.bookingRepository = bookingRepository;
        this.bookingCommandService = bookingCommandService;
        this.paymentCommandService = paymentCommandService;
        this.seedEnabled = seedEnabled;
    }

    @Order(20)
    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedEnabled || bookingRepository.count() > 0) return;
        var bookings = read("seed/bookings.json", BookingsFile.class).bookings();
        var payments = read("seed/payments.json", PaymentsFile.class).payments();
        int savedBookings = 0;
        for (var booking : bookings)
            savedBookings += report(bookingCommandService.handle(new CreateBookingCommand(booking)), booking.code());
        int savedPayments = 0;
        for (var payment : payments)
            savedPayments += report(paymentCommandService.handle(payment), payment.reference());
        LOGGER.info("Seeded {} bookings and {} payments", savedBookings, savedPayments);
    }

    private <T> T read(String path, Class<T> type) {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return jsonMapper.readValue(input, type);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read the seed file " + path, e);
        }
    }

    private static int report(Result<?, ?> result, String reference) {
        if (result instanceof Result.Success<?, ?>) return 1;
        LOGGER.warn("Seed row {} was not loaded: {}", reference, result);
        return 0;
    }
}
