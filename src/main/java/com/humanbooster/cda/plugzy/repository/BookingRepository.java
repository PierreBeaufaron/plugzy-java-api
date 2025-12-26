package com.humanbooster.cda.plugzy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface Booking extends JpaRepository<Booking, UUID> {
    List<Booking> findByChargingStation_Id(UUID id);
    List<Booking> findByUser_Id(UUID id);
}
