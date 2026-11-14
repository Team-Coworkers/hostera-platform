package com.teamcoworkers.hostera.platform.rooms.application.internal.queryservices;

import com.teamcoworkers.hostera.platform.rooms.application.queries.GetAllStatusPeriodsQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queries.GetStatusPeriodByIdQuery;
import com.teamcoworkers.hostera.platform.rooms.application.queryservices.StatusPeriodQueryService;
import com.teamcoworkers.hostera.platform.rooms.domain.model.aggregates.StatusPeriod;
import com.teamcoworkers.hostera.platform.rooms.domain.repositories.StatusPeriodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusPeriodQueryServiceImpl implements StatusPeriodQueryService {
    private final StatusPeriodRepository statusPeriodRepository;

    public StatusPeriodQueryServiceImpl(StatusPeriodRepository statusPeriodRepository) {
        this.statusPeriodRepository = statusPeriodRepository;
    }

    @Override
    public List<StatusPeriod> handle(GetAllStatusPeriodsQuery query) {
        var statusPeriods = query.propertyId() == null
                ? statusPeriodRepository.findAll()
                : statusPeriodRepository.findAllByPropertyId(query.propertyId());
        return statusPeriods.stream()
                .filter(statusPeriod -> query.roomId() == null || query.roomId().equals(statusPeriod.getRoomId()))
                .filter(statusPeriod -> query.status() == null || query.status().equalsIgnoreCase(statusPeriod.getStatus().value()))
                .toList();
    }

    @Override
    public Optional<StatusPeriod> handle(GetStatusPeriodByIdQuery query) {
        return statusPeriodRepository.findById(query.statusPeriodId());
    }
}
