package com.humanbooster.cda.plugzy.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String zipCode;
    @Column(nullable = false)
    private String city;
    private Double latitude;
    private Double longitude;
    @Column(nullable = false, unique = true)
    private String gmapId;

    public Location() {
    }

    public Location(String address, String zipCode, String city, Double latitude, Double longitude, String gmapId) {
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gmapId = gmapId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getGmapId() {
        return gmapId;
    }

    public void setGmapId(String gmapId) {
        this.gmapId = gmapId;
    }
}
