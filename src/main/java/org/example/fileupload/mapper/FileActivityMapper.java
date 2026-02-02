package org.example.fileupload.mapper;

// FileActivityMapper.java

import org.example.fileupload.dto.FileActivityDto;
import org.example.fileupload.dto.UserSummaryDto;
import org.example.fileupload.entity.FileActivity;
import org.example.fileupload.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface FileActivityMapper {

    @Mapping(target = "action", expression = "java(activity.getAction() != null ? activity.getAction().name() : null)")
    @Mapping(target = "performedBy", source = "performedBy", qualifiedByName = "userToSummary")  // minimal mapping
    FileActivityDto toDto(FileActivity activity);


    @Named("userToSummary")
    default UserSummaryDto userToSummary(User user) {
        if (user == null) return null;
        return UserSummaryDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}