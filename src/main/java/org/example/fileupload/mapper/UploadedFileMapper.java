package org.example.fileupload.mapper;

import org.example.fileupload.dto.FileDetailDto;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.UploadedFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {UserMapper.class, FileActivityMapper.class})
public interface UploadedFileMapper {

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "fileUrl", expression = "java(\"/api/files/download/\" + uploadedFile.getStoredFileName())")
    UploadedFileDto toDto(UploadedFile uploadedFile);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "activities", source = "activities")
    FileDetailDto toDetailDto(UploadedFile uploadedFile);
}