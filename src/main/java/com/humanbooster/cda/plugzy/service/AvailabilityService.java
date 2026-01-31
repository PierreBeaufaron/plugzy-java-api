package com.humanbooster.cda.plugzy.service;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AvailabilityService {
    boolean isStationAvailable(UUID stationId, LocalDateTime start, LocalDateTime end);
}
