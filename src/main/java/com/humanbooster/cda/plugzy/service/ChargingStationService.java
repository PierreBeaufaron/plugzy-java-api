package com.humanbooster.cda.plugzy.service;

import com.humanbooster.cda.plugzy.controller.dto.common.PagedResponse;
import com.humanbooster.cda.plugzy.controller.dto.station.StationDetailsDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationListItemDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationMapMarkerDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationMarkerDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChargingStationService {
    List<StationMarkerDTO> getAllActive();

    List<StationMarkerDTO> search(double lat, double lng, double radiusKm, Double minPower, Double maxPower, Boolean freeStanding);

    List<StationMapMarkerDTO> getMapMarkers(double swLat, double swLng, double neLat, double neLng,
                                            Double minPower, Double maxPower, Boolean freeStanding);

    PagedResponse<StationListItemDTO> getList(double swLat, double swLng, double neLat, double neLng,
                                              Double minPower, Double maxPower, Boolean freeStanding,
                                              Pageable pageable);

    StationDetailsDTO getOne(UUID id);
}
