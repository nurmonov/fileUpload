package org.example.fileupload.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FileMonitoringDto {
    private UploadedFileDto file;
    private List<FileActivityDto> recentActivities; // So'nggi 5 activity
    private String currentStatus;
}
