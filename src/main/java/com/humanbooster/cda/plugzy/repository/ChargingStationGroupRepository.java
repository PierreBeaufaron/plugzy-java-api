package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.ChargingStationGroup;
import com.humanbooster.cda.plugzy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChargingStationGroupRepository extends JpaRepository<ChargingStationGroup, UUID> {
    Optional<ChargingStationGroup> findByOwner(User owner);
    Optional<ChargingStationGroup> findByOwnerAndTitle(User owner, String title);
}
