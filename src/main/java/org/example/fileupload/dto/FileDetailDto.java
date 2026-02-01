package org.example.fileupload.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDetailDto {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;

    private UserSummaryDto owner;
    // private Set<UserSummaryDto> usersWithAccess;   ← BU QATORNI OLIB TASHLANG

    private List<FileActivityDto> activities;
    private String fileUrl;
}

