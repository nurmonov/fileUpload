package org.example.fileupload.mapper;

import org.example.fileupload.dto.FileActivityDto;
import org.example.fileupload.dto.FileDetailDto;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.FileActivity;
import org.example.fileupload.entity.UploadedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {UserMapper.class, FileActivityMapper.class})
public interface UploadedFileMapper {

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "usersWithAccess", source = "usersWithAccess")
    UploadedFileDto toDto(UploadedFile uploadedFile);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "usersWithAccess", source = "usersWithAccess")
    @Mapping(target = "activities", source = "activities")
    FileDetailDto toDetailDto(UploadedFile uploadedFile);
}
