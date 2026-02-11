package org.example.fileupload.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileDto {

    private Integer id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;
    private String asos;
    private String ishlatilihsi;

    private UserSummaryDto owner;
    private List<UserSummaryDto> usersWithAccess;


    private String status;
    private String fileUrl;
}