package com.humanbooster.cda.plugzy.service.impl;

import com.humanbooster.cda.plugzy.repository.BookingRepository;
import com.humanbooster.cda.plugzy.repository.ChargingStationRepository;
import com.humanbooster.cda.plugzy.service.AvailabilityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final BookingRepository bookingRepository;
    private final ChargingStationRepository chargingStationRepository;

    public AvailabilityServiceImpl(BookingRepository bookingRepository,
                                   ChargingStationRepository chargingStationRepository) {
        this.bookingRepository = bookingRepository;
        this.chargingStationRepository = chargingStationRepository;
    }

    @Override
    public boolean isStationAvailable(UUID stationId, LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return false;
        if (!start.isBefore(end)) return false;

        var stationOpt = chargingStationRepository.findById(stationId);
        if (stationOpt.isEmpty()) return false;

        var station = stationOpt.get();
        if (!station.isActive()) return false;

        boolean overlaps = bookingRepository
                .existsByChargingStation_IdAndStartTimeLessThanAndEndTimeGreaterThan(stationId, end, start);

        return !overlaps;
    }
}
