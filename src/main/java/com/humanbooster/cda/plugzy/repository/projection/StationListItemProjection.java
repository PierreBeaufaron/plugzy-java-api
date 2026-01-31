package com.humanbooster.cda.plugzy.repository.projection;

import java.util.UUID;

public interface StationListItemProjection {
    UUID getId();
    String getName();
    Double getPower();
    Double getPrice();
    Boolean getFreeStanding();

    String getAddress();
    String getZipCode();
    String getCity();
}
