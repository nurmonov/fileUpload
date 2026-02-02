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
    private String filePath;           // agar frontendga kerak bo'lsa (lekin odatda yashirin)
    private LocalDateTime uploadDate;

    private UserSummaryDto owner;
    private Set<UserSummaryDto> usersWithAccess;

    // Yangi qo'shilgan maydon – frontend uchun yuklab olish havolasi
    private String fileUrl;            // masalan: "/api/files/download/uuid_filename.pdf"
}