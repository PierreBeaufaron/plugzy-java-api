package com.humanbooster.cda.plugzy.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "charging_station")
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double power;
    @Column(nullable = false)
    private Double price;
    private boolean isActive = true;
    private boolean freeStanding = false;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private ChargingStationGroup stationGroup;

    public ChargingStation() {
    }

    public ChargingStation(String name, Double power, Double price, boolean isActive, boolean freeStanding, ChargingStationGroup stationGroup) {
        this.name = name;
        this.power = power;
        this.price = price;
        this.isActive = isActive;
        this.freeStanding = freeStanding;
        this.stationGroup = stationGroup;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFreeStanding() {
        return freeStanding;
    }

    public void setFreeStanding(boolean freeStanding) {
        this.freeStanding = freeStanding;
    }

    public ChargingStationGroup getGroup() {
        return stationGroup;
    }

    public void setGroup(ChargingStationGroup group) {
        this.stationGroup = group;
    }
}
