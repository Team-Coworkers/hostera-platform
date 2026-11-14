package com.teamcoworkers.hostera.platform.rooms.application.internal.commandservices;

import com.teamcoworkers.hostera.platform.organizations.interfaces.acl.OrganizationsContextFacade;
import com.teamcoworkers.hostera.platform.rooms.application.commands.CreateDailyRateCommand;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RoomType;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.ActivationStatus;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.IncludedServices;
import com.teamcoworkers.hostera.platform.rooms.domain.model.valueobjects.NightlyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.DailyRateRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyRateCommandServiceImplTest {
    private static final LocalDate NIGHT = LocalDate.of(2026, 10, 1);

    @Mock
    private DailyRateRepository dailyRateRepository;

    @Mock
    private RatePlanRepository ratePlanRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private OrganizationsContextFacade organizationsContextFacade;

    @InjectMocks
    private DailyRateCommandServiceImpl service;

    @BeforeEach
    void setUp() {
        when(organizationsContextFacade.existsPropertyById(1L)).thenReturn(true);
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(RatePlan.reconstitute(1L, 1L, "Flexible", List.of(),
                IncludedServices.ROOM_ONLY, true, "Free cancellation until 24 hours before arrival", ActivationStatus.ACTIVE)));
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(RoomType.reconstitute(1L, 1L, "Standard", 2, "1 queen",
                new NightlyRate(new BigDecimal("180")), ActivationStatus.ACTIVE)));
    }

    @Test
    void createsThePriceOfANight() {
        when(dailyRateRepository.existsByRatePlanIdAndRoomTypeIdAndDate(1L, 1L, NIGHT)).thenReturn(false);
        when(dailyRateRepository.save(any(DailyRate.class))).thenAnswer(call -> call.getArgument(0));

        var result = service.handle(new CreateDailyRateCommand(1L, 1L, 1L, "2026-10-01", new BigDecimal("190")));

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.toOptional().orElseThrow().getDate()).isEqualTo(NIGHT);
    }

    @Test
    void rejectsASecondPriceForTheSameRoomTypeAndNight() {
        when(dailyRateRepository.existsByRatePlanIdAndRoomTypeIdAndDate(1L, 1L, NIGHT)).thenReturn(true);

        var result = service.handle(new CreateDailyRateCommand(1L, 1L, 1L, "2026-10-01", new BigDecimal("190")));

        assertThat(result.isFailure()).isTrue();
        verify(dailyRateRepository, never()).save(any());
    }

    @Test
    void rejectsAnAmountThatIsNotGreaterThanZero() {
        when(dailyRateRepository.existsByRatePlanIdAndRoomTypeIdAndDate(1L, 1L, NIGHT)).thenReturn(false);

        var result = service.handle(new CreateDailyRateCommand(1L, 1L, 1L, "2026-10-01", BigDecimal.ZERO));

        assertThat(result.isFailure()).isTrue();
        verify(dailyRateRepository, never()).save(any());
    }
}
