package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.Booking;
import com.humanbooster.cda.plugzy.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findByChargingStation_Id(UUID id);
    List<Booking> findByUser_Id(UUID id);

    boolean existsByChargingStationAndStartTimeAndEndTime(
            ChargingStation chargingStation,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    boolean existsByChargingStation_IdAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID stationId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
