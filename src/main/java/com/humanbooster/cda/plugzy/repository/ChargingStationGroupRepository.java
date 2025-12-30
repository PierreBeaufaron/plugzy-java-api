package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.ChargingStationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChargingStationGroupRepository extends JpaRepository<ChargingStationGroup, UUID> {
}
