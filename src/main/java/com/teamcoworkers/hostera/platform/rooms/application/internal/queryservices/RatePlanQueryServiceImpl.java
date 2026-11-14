package com.teamcoworkers.hostera.platform.rooms.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllRatePlansQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetRatePlanByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.RatePlanQueryService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.RatePlan;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.RatePlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatePlanQueryServiceImpl implements RatePlanQueryService {
    private final RatePlanRepository ratePlanRepository;

    public RatePlanQueryServiceImpl(RatePlanRepository ratePlanRepository) {
        this.ratePlanRepository = ratePlanRepository;
    }

    @Override
    public List<RatePlan> handle(GetAllRatePlansQuery query) {
        var ratePlans = query.propertyId() == null
                ? ratePlanRepository.findAll()
                : ratePlanRepository.findAllByPropertyId(query.propertyId());
        return ratePlans.stream()
                .filter(ratePlan -> query.status() == null || query.status().equalsIgnoreCase(ratePlan.getStatus().value()))
                .toList();
    }

    @Override
    public Optional<RatePlan> handle(GetRatePlanByIdQuery query) {
        return ratePlanRepository.findById(query.ratePlanId());
    }
}
