package org.example.fileupload.dto;



import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private Set<UserSummaryDto> usersWithAccess;

    private List<FileActivityDto> activities;   // timeline
}

