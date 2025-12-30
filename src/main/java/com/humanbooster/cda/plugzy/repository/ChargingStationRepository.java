package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
    List<ChargingStation> findByStationGroup_Id(UUID id);
}
