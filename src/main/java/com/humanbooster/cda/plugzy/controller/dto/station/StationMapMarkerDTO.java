package com.humanbooster.cda.plugzy.controller.dto.station;

import java.util.UUID;

public class StationMapMarkerDTO {
    private UUID id;
    private Double latitude;
    private Double longitude;

    public StationMapMarkerDTO() {}

    public StationMapMarkerDTO(UUID id, Double latitude, Double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
