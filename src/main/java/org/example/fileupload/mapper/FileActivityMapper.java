package org.example.fileupload.mapper;

// FileActivityMapper.java

import org.example.fileupload.dto.FileActivityDto;
import org.example.fileupload.entity.FileActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface FileActivityMapper {

    @Mapping(target = "action", expression = "java(activity.getAction() != null ? activity.getAction().name() : null)")
    @Mapping(target = "performedBy", source = "performedBy")
    FileActivityDto toDto(FileActivity activity);
}