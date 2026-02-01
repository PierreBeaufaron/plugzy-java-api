package com.humanbooster.cda.plugzy.service.impl;

import com.humanbooster.cda.plugzy.entity.ChargingStation;
import com.humanbooster.cda.plugzy.repository.BookingRepository;
import com.humanbooster.cda.plugzy.repository.ChargingStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ChargingStationRepository chargingStationRepository;

    AvailabilityServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AvailabilityServiceImpl(bookingRepository, chargingStationRepository);
    }

    @Test
    void returnsFalse_whenStartIsNull() {
        UUID stationId = UUID.randomUUID();
        assertFalse(service.isStationAvailable(stationId, null, LocalDateTime.now().plusHours(1)));

        verifyNoInteractions(chargingStationRepository, bookingRepository);
    }

    @Test
    void returnsFalse_whenEndIsNull() {
        UUID stationId = UUID.randomUUID();
        assertFalse(service.isStationAvailable(stationId, LocalDateTime.now(), null));

        verifyNoInteractions(chargingStationRepository, bookingRepository);
    }

    @Test
    void returnsFalse_whenStartIsAfterOrEqualEnd() {
        UUID stationId = UUID.randomUUID();
        LocalDateTime t = LocalDateTime.of(2026, 2, 18, 14, 0);

        assertFalse(service.isStationAvailable(stationId, t, t));
        assertFalse(service.isStationAvailable(stationId, t.plusHours(1), t));

        verifyNoInteractions(chargingStationRepository, bookingRepository);
    }

    @Test
    void returnsFalse_whenStationDoesNotExist() {
        UUID stationId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 2, 18, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 18, 18, 0);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.empty());

        assertFalse(service.isStationAvailable(stationId, start, end));

        verify(chargingStationRepository).findById(stationId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void returnsFalse_whenStationIsInactive() {
        UUID stationId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 2, 18, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 18, 18, 0);

        ChargingStation station = new ChargingStation();
        station.setActive(false);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));

        assertFalse(service.isStationAvailable(stationId, start, end));

        verify(chargingStationRepository).findById(stationId);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void returnsFalse_whenOverlapsExistingBooking() {
        UUID stationId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 2, 18, 15, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 18, 16, 0);

        ChargingStation station = new ChargingStation();
        station.setActive(true);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(bookingRepository.existsByChargingStation_IdAndStartTimeLessThanAndEndTimeGreaterThan(
                stationId, end, start
        )).thenReturn(true);

        assertFalse(service.isStationAvailable(stationId, start, end));

        verify(chargingStationRepository).findById(stationId);
        verify(bookingRepository).existsByChargingStation_IdAndStartTimeLessThanAndEndTimeGreaterThan(stationId, end, start);
    }

    @Test
    void returnsTrue_whenNoOverlapAndStationActive() {
        UUID stationId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2026, 2, 18, 18, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 18, 19, 0);

        ChargingStation station = new ChargingStation();
        station.setActive(true);

        when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(station));
        when(bookingRepository.existsByChargingStation_IdAndStartTimeLessThanAndEndTimeGreaterThan(
                stationId, end, start
        )).thenReturn(false);

        assertTrue(service.isStationAvailable(stationId, start, end));

        verify(chargingStationRepository).findById(stationId);
        verify(bookingRepository).existsByChargingStation_IdAndStartTimeLessThanAndEndTimeGreaterThan(stationId, end, start);
    }
}
