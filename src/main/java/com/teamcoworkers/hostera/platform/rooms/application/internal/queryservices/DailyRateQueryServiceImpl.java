package com.teamcoworkers.hostera.platform.rooms.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllDailyRatesQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetDailyRateByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.DailyRateQueryService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.DailyRate;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.DailyRateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DailyRateQueryServiceImpl implements DailyRateQueryService {
    private final DailyRateRepository dailyRateRepository;

    public DailyRateQueryServiceImpl(DailyRateRepository dailyRateRepository) {
        this.dailyRateRepository = dailyRateRepository;
    }

    @Override
    public List<DailyRate> handle(GetAllDailyRatesQuery query) {
        var dailyRates = query.propertyId() == null
                ? dailyRateRepository.findAll()
                : dailyRateRepository.findAllByPropertyId(query.propertyId());
        return dailyRates.stream()
                .filter(dailyRate -> query.ratePlanId() == null || query.ratePlanId().equals(dailyRate.getRatePlanId()))
                .filter(dailyRate -> query.roomTypeId() == null || query.roomTypeId().equals(dailyRate.getRoomTypeId()))
                .filter(dailyRate -> query.date() == null || query.date().equals(dailyRate.getDate().toString()))
                .toList();
    }

    @Override
    public Optional<DailyRate> handle(GetDailyRateByIdQuery query) {
        return dailyRateRepository.findById(query.dailyRateId());
    }
}
