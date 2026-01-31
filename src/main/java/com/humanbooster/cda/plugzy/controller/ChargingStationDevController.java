package com.humanbooster.cda.plugzy.controller;

import com.humanbooster.cda.plugzy.controller.dto.station.StationMarkerDTO;
import com.humanbooster.cda.plugzy.service.ChargingStationService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("dev")
@RestController
@RequestMapping("/api/stations")
public class ChargingStationDevController {

    private final ChargingStationService stationService;

    public ChargingStationDevController(ChargingStationService stationService) {
        this.stationService = stationService;
    }

    // Toutes les bornes actives (markers)
    @GetMapping
    public List<StationMarkerDTO> getAll() {
        return stationService.getAllActive();
    }

    // Recherche géoloc + filtres (markers)
    @GetMapping("/search")
    public List<StationMarkerDTO> search(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lng,
            @RequestParam(defaultValue = "10") double radiusKm,
            @RequestParam(required = false) Double minPower,
            @RequestParam(required = false) Double maxPower,
            @RequestParam(required = false) Boolean freeStanding
    ) {
        return stationService.search(lat, lng, radiusKm, minPower, maxPower, freeStanding);
    }
}
