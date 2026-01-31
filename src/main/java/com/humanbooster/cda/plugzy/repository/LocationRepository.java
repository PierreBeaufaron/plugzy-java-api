package com.humanbooster.cda.plugzy.repository;

import com.humanbooster.cda.plugzy.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    Optional<Location> findByGmapId(String gmapId);
}
