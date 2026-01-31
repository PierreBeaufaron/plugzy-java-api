package com.humanbooster.cda.plugzy.controller;

import com.humanbooster.cda.plugzy.controller.dto.booking.AvailabilityRequestDTO;
import com.humanbooster.cda.plugzy.controller.dto.booking.AvailabilityResponseDTO;
import com.humanbooster.cda.plugzy.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stations")
public class StationAvailabilityController {

    private final AvailabilityService availabilityService;

    public StationAvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponseDTO> check(
            @PathVariable("id") UUID stationId,
            @RequestBody AvailabilityRequestDTO req
    ) {
        boolean available = availabilityService.isStationAvailable(stationId, req.getStart(), req.getEnd());
        return ResponseEntity.ok(new AvailabilityResponseDTO(available));
    }
}
