package org.example.fileupload.mapper;

// UserMapper.java (MapStruct ishlatib, mukammal va avtomatik mapping uchun)

import org.example.fileupload.dto.UserDto;
import org.example.fileupload.dto.UserSummaryDto;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    UserSummaryDto toSummaryDto(User user);

    default String map(Role role) {
        return role != null ? role.name() : null;
    }
}