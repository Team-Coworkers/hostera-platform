package com.teamcoworkers.hostera.platform.rooms.application.internal.eventhandlers;

import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.Room;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.DateRange;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.IncludedServices;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.OperationalStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.DailyRateRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.StatusPeriodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Loads the demonstration room types, rooms, rate plans, daily rates and status periods of the
 * Hostera frontend on an empty database, so the deployed application does not start empty. The
 * rows keep the order of {@code server/data} of the frontend, so their generated identifiers
 * match the ones that the other resources reference. It runs after the organizations seed.
 */
@Service
public class RoomsSeedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomsSeedEventHandler.class);
    private static final String DAILY_RATES_FILE = "seed/daily-rates.csv";

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final RatePlanRepository ratePlanRepository;
    private final DailyRateRepository dailyRateRepository;
    private final StatusPeriodRepository statusPeriodRepository;
    private final boolean seedEnabled;

    public RoomsSeedEventHandler(RoomTypeRepository roomTypeRepository, RoomRepository roomRepository,
                                 RatePlanRepository ratePlanRepository, DailyRateRepository dailyRateRepository,
                                 StatusPeriodRepository statusPeriodRepository,
                                 @Value("${app.seed.enabled:true}") boolean seedEnabled) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.ratePlanRepository = ratePlanRepository;
        this.dailyRateRepository = dailyRateRepository;
        this.statusPeriodRepository = statusPeriodRepository;
        this.seedEnabled = seedEnabled;
    }

    @Order(20)
    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedEnabled || roomTypeRepository.count() > 0) return;
        try {
            seedRoomTypes();
            seedRooms();
            seedRatePlans();
            seedStatusPeriods();
            var dailyRates = seedDailyRates();
            LOGGER.info("Seeded the rooms context with {} daily rates", dailyRates);
        } catch (RuntimeException | IOException e) {
            LOGGER.warn("The rooms seed stopped: {}", e.getMessage());
        }
    }

    private void seedRoomTypes() {
        // propertyId, name, capacity, bedConfiguration, baseNightlyRate, status
        String[][] roomTypes = {
                {"1", "Standard", "2", "1 queen", "180", "active"},
                {"1", "Twin", "2", "2 single", "200", "active"},
                {"1", "Deluxe", "4", "1 king + sofa bed", "290", "active"},
                {"1", "Suite", "4", "1 king + sofa bed", "420", "active"},
                {"1", "Family", "5", "2 double + 1 single", "340", "inactive"},
                {"2", "Standard", "2", "1 queen", "150", "active"},
                {"2", "Deluxe", "3", "1 king + 1 single", "240", "active"},
        };
        for (var row : roomTypes)
            roomTypeRepository.save(new RoomType(Long.valueOf(row[0]), row[1], Integer.valueOf(row[2]), row[3],
                    new NightlyRate(new BigDecimal(row[4])), ActivationStatus.fromValue(row[5])));
    }

    private void seedRooms() {
        // propertyId, number, roomTypeId, floor
        String[][] rooms = {
                {"1", "201", "1", "2"},
                {"1", "203", "2", "2"},
                {"1", "210", "1", "2"},
                {"1", "214", "2", "2"},
                {"1", "301", "1", "3"},
                {"1", "305", "3", "3"},
                {"1", "308", "2", "3"},
                {"1", "312", "1", "3"},
                {"1", "402", "1", "4"},
                {"1", "407", "2", "4"},
                {"1", "410", "3", "4"},
                {"1", "412", "1", "4"},
                {"1", "501", "1", "5"},
                {"1", "507", "3", "5"},
                {"1", "509", "5", "5"},
                {"1", "601", "4", "6"},
                {"1", "608", "4", "6"},
                {"2", "101", "6", "1"},
                {"2", "102", "6", "1"},
                {"2", "103", "7", "1"},
                {"2", "201", "6", "2"},
                {"2", "202", "7", "2"},
        };
        for (var row : rooms)
            roomRepository.save(new Room(Long.valueOf(row[0]), row[1], Long.valueOf(row[2]), Integer.valueOf(row[3])));
    }

    private void seedRatePlans() {
        // propertyId, name, roomTypeIds (separated by ;), includedServices, refundable, cancellationPolicy, status
        String[][] ratePlans = {
                {"1", "Flexible", "", "room-only", "true", "Free cancellation until 24 hours before arrival", "active"},
                {"1", "Breakfast included", "", "breakfast-included", "true", "Free cancellation until 48 hours before arrival", "active"},
                {"1", "Non-refundable", "", "room-only", "false", "", "active"},
                {"1", "Honeymoon", "4", "half-board", "true", "Free cancellation until 7 days before arrival", "active"},
                {"1", "Summer 2026", "1;2;3", "breakfast-included", "true", "Free cancellation until 72 hours before arrival", "inactive"},
                {"2", "Flexible", "", "room-only", "true", "Free cancellation until 24 hours before arrival", "active"},
        };
        for (var row : ratePlans) {
            List<Long> roomTypeIds = row[2].isEmpty() ? List.of() : Arrays.stream(row[2].split(";")).map(Long::valueOf).toList();
            ratePlanRepository.save(new RatePlan(Long.valueOf(row[0]), row[1], roomTypeIds, IncludedServices.fromValue(row[3]),
                    Boolean.valueOf(row[4]), row[5], ActivationStatus.fromValue(row[6])));
        }
    }

    private void seedStatusPeriods() {
        // propertyId, roomId, status, startDate, endDate, reason
        String[][] statusPeriods = {
                {"1", "12", "blocked", "2026-10-10", "2026-10-11", "Held for a corporate group"},
                {"1", "14", "out-of-service", "2026-10-11", "2026-10-14", "Bathroom plumbing repair"},
                {"1", "17", "out-of-service", "2026-10-11", "2026-10-12", "Air conditioning maintenance"},
                {"1", "4", "needs-cleaning", "2026-10-06", "2026-10-06", "Deep cleaning after checkout"},
                {"1", "6", "blocked", "2026-10-09", "2026-10-09", "Staff training"},
                {"1", "15", "out-of-service", "2026-10-01", "2026-10-20", "Renovation"},
                {"1", "2", "blocked", "2026-10-05", "2026-10-07", "Held for a VIP guest"},
                {"1", "11", "needs-cleaning", "2026-10-13", "2026-10-13", "Deep cleaning after checkout"},
                {"2", "19", "blocked", "2026-10-06", "2026-10-07", "Owner use"},
        };
        for (var row : statusPeriods)
            statusPeriodRepository.save(new StatusPeriod(Long.valueOf(row[0]), Long.valueOf(row[1]), OperationalStatus.fromValue(row[2]),
                    DateRange.of(row[3], row[4]), row[5]));
    }

    /** Reads propertyId, ratePlanId, roomTypeId, date and amount from the classpath file. */
    private int seedDailyRates() throws IOException {
        var count = 0;
        try (var reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(DAILY_RATES_FILE).getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            for (var line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.isBlank()) continue;
                var row = line.trim().split(",");
                dailyRateRepository.save(new DailyRate(Long.valueOf(row[0]), Long.valueOf(row[1]), Long.valueOf(row[2]),
                        LocalDate.parse(row[3]), new NightlyRate(new BigDecimal(row[4]))));
                count++;
            }
        }
        return count;
    }
}
