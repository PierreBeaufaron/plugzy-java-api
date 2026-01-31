package com.humanbooster.cda.plugzy.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "charging_station_group")
public class ChargingStationGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public ChargingStationGroup() {
    }

    public ChargingStationGroup(String title, String description, User owner, Location location) {
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
