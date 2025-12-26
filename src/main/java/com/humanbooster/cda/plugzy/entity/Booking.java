package com.humanbooster.cda.plugzy.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private Double totalPrice;
    @Column(nullable = false)
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "charging_station_id", nullable = false)
    private ChargingStation chargingStation;

    public Booking() {
    }

    public Booking(LocalDateTime startTime, LocalDateTime endTime, Double totalPrice, Integer status, User user, ChargingStation chargingStation) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.user = user;
        this.chargingStation = chargingStation;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChargingStation getChargingStation() {
        return chargingStation;
    }

    public void setChargingStation(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
    }
}
