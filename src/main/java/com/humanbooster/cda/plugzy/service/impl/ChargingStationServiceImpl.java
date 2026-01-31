package com.humanbooster.cda.plugzy.service.impl;

import com.humanbooster.cda.plugzy.controller.dto.common.PagedResponse;
import com.humanbooster.cda.plugzy.controller.dto.mapper.ChargingStationMapper;
import com.humanbooster.cda.plugzy.controller.dto.station.StationDetailsDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationListItemDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationMapMarkerDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationMarkerDTO;
import com.humanbooster.cda.plugzy.entity.ChargingStation;
import com.humanbooster.cda.plugzy.repository.ChargingStationRepository;
import com.humanbooster.cda.plugzy.repository.projection.StationListItemProjection;
import com.humanbooster.cda.plugzy.repository.projection.StationMapMarkerProjection;
import com.humanbooster.cda.plugzy.service.ChargingStationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ChargingStationServiceImpl implements ChargingStationService {

    private final ChargingStationRepository stationRepository;
    private final ChargingStationMapper mapper;

    public ChargingStationServiceImpl(ChargingStationRepository stationRepository, ChargingStationMapper mapper) {
        this.stationRepository = stationRepository;
        this.mapper = mapper;
    }

    @Override
    public List<StationMarkerDTO> getAllActive() {
        return stationRepository.findAllActiveWithLocation()
                .stream()
                .map(mapper::toMarkerDto)
                .toList();
    }

    @Override
    public List<StationMarkerDTO> search(double lat, double lng, double radiusKm, Double minPower, Double maxPower, Boolean freeStanding) {
        if (radiusKm <= 0 || radiusKm > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radiusKm must be between 0 and 200");
        }
        if (minPower != null && maxPower != null && minPower > maxPower) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPower cannot be greater than maxPower");
        }

        return stationRepository.searchActiveWithinRadius(lat, lng, radiusKm, minPower, maxPower, freeStanding)
                .stream()
                .map(mapper::toMarkerDto)
                .toList();
    }

    @Override
    public List<StationMapMarkerDTO> getMapMarkers(double swLat, double swLng, double neLat, double neLng,
                                                   Double minPower, Double maxPower, Boolean freeStanding) {
        validateBounds(swLat, swLng, neLat, neLng);
        validateFilters(minPower, maxPower);

        List<StationMapMarkerProjection> rows =
                stationRepository.findMarkersInBounds(swLat, swLng, neLat, neLng, minPower, maxPower, freeStanding);

        return rows.stream()
                .map(r -> new StationMapMarkerDTO(r.getId(), r.getLatitude(), r.getLongitude()))
                .toList();
    }

    @Override
    public PagedResponse<StationListItemDTO> getList(double swLat, double swLng, double neLat, double neLng,
                                                     Double minPower, Double maxPower, Boolean freeStanding,
                                                     Pageable pageable) {
        validateBounds(swLat, swLng, neLat, neLng);
        validateFilters(minPower, maxPower);

        if (pageable.getPageSize() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size must be <= 100");
        }

        Page<StationListItemProjection> page =
                stationRepository.findListInBounds(swLat, swLng, neLat, neLng, minPower, maxPower, freeStanding, pageable);

        List<StationListItemDTO> items = page.getContent().stream().map(p -> {
            StationListItemDTO dto = new StationListItemDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setPower(p.getPower());
            dto.setPrice(p.getPrice());
            dto.setFreeStanding(Boolean.TRUE.equals(p.getFreeStanding()));
            dto.setAddress(p.getAddress());
            dto.setZipCode(p.getZipCode());
            dto.setCity(p.getCity());
            return dto;
        }).toList();

        return new PagedResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public StationDetailsDTO getOne(UUID id) {
        ChargingStation station = stationRepository.findOneWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Charging station not found"));
        return mapper.toDetailsDto(station);
    }

    private void validateFilters(Double minPower, Double maxPower) {
        if (minPower != null && maxPower != null && minPower > maxPower) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPower cannot be greater than maxPower");
        }
        if (minPower != null && minPower < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPower must be >= 0");
        }
        if (maxPower != null && maxPower < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxPower must be >= 0");
        }
    }

    private void validateBounds(double swLat, double swLng, double neLat, double neLng) {
        // lat/lng ranges
        if (swLat < -90 || swLat > 90 || neLat < -90 || neLat > 90 || swLng < -180 || swLng > 180 || neLng < -180 || neLng > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid bounds coordinates");
        }
        // simple sanity
        if (swLat > neLat) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "swLat must be <= neLat");
        }
        // Note: crossing antimeridian (swLng > neLng) non géré MVP
        if (swLng > neLng) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "swLng must be <= neLng (antimeridian not supported yet)");
        }
    }
}
