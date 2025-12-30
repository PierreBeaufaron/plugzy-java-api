package com.humanbooster.cda.plugzy.controller.dto.mapper;

import com.humanbooster.cda.plugzy.controller.dto.user.UserPublicDTO;
import com.humanbooster.cda.plugzy.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(source = "role.name", target = "role")
    UserPublicDTO convertToDTO(User user);
}
