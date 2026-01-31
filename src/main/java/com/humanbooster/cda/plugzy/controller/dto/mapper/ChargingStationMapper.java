package com.humanbooster.cda.plugzy.controller.dto.mapper;

import com.humanbooster.cda.plugzy.controller.dto.station.StationDetailsDTO;
import com.humanbooster.cda.plugzy.controller.dto.station.StationMarkerDTO;
import com.humanbooster.cda.plugzy.entity.ChargingStation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChargingStationMapper {

    @Mapping(target = "latitude", source = "group.location.latitude")
    @Mapping(target = "longitude", source = "group.location.longitude")
    @Mapping(target = "address", source = "group.location.address")
    @Mapping(target = "city", source = "group.location.city")
    @Mapping(target = "zipCode", source = "group.location.zipCode")
    StationMarkerDTO toMarkerDto(ChargingStation station);

    @Mapping(target = "active", source = "active")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupTitle", source = "group.title")
    @Mapping(target = "groupDescription", source = "group.description")

    @Mapping(target = "locationId", source = "group.location.id")
    @Mapping(target = "address", source = "group.location.address")
    @Mapping(target = "city", source = "group.location.city")
    @Mapping(target = "zipCode", source = "group.location.zipCode")
    @Mapping(target = "latitude", source = "group.location.latitude")
    @Mapping(target = "longitude", source = "group.location.longitude")
    @Mapping(target = "gmapId", source = "group.location.gmapId")
    StationDetailsDTO toDetailsDto(ChargingStation station);
}
