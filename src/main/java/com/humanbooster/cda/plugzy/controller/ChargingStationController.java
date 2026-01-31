package com.humanbooster.cda.plugzy.controller;

import com.humanbooster.cda.plugzy.controller.dto.common.PagedResponse;
import com.humanbooster.cda.plugzy.controller.dto.station.StationDetailsDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationListItemDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationMapMarkerDTO;
import com.humanbooster.cda.plugzy.service.ChargingStationService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/stations")
public class ChargingStationController {

    private final ChargingStationService stationService;

    public ChargingStationController(ChargingStationService stationService) {
        this.stationService = stationService;
    }

    // MAP : markers (non paginé)
    @GetMapping("/map")
    public List<StationMapMarkerDTO> map(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double swLat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double swLng,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double neLat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double neLng,
            @RequestParam(required = false) Double minPower,
            @RequestParam(required = false) Double maxPower,
            @RequestParam(required = false) Boolean freeStanding
    ) {
        return stationService.getMapMarkers(swLat, swLng, neLat, neLng, minPower, maxPower, freeStanding);
    }

    // LIST : paginée
    @GetMapping("/list")
    public PagedResponse<StationListItemDTO> list(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double swLat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double swLng,
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double neLat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double neLng,
            @RequestParam(required = false) Double minPower,
            @RequestParam(required = false) Double maxPower,
            @RequestParam(required = false) Boolean freeStanding,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return stationService.getList(swLat, swLng, neLat, neLng, minPower, maxPower, freeStanding, pageable);
    }


    // Détail d’une borne
    @GetMapping("/{id}")
    public StationDetailsDTO getOne(@PathVariable UUID id) {
        return stationService.getOne(id);
    }
}
