package com.humanbooster.cda.plugzy.repository.projection;

import java.util.UUID;

public interface StationMapMarkerProjection {
    UUID getId();
    Double getLatitude();
    Double getLongitude();
}
