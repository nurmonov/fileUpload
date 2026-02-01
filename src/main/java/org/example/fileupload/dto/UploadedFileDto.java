package org.example.fileupload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileDto {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;
    private UserSummaryDto owner;
    private String fileUrl;

    // BU YERNI OLIB TASHLANG yoki quyidagicha qiling:
    // @JsonIgnore
    // private Set<UserSummaryDto> usersWithAccess;
}