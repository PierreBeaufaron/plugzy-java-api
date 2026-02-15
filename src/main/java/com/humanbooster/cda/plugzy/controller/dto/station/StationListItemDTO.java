package com.humanbooster.cda.plugzy.controller.dto.station;

import java.util.UUID;

public class StationListItemDTO {
    private UUID id;
    private String name;
    private Double power;
    private Double price;
    private Boolean freeStanding;

    private String address;
    private String zipCode;
    private String city;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPower() { return power; }
    public void setPower(Double power) { this.power = power; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isFreeStanding() { return freeStanding; }
    public void setFreeStanding(boolean freeStanding) { this.freeStanding = freeStanding; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
