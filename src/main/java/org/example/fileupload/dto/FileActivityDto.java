package org.example.fileupload.dto;



import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileActivityDto {

    private Integer id;
    private UserSummaryDto performedBy;
    private String action;
    private String details;
    private LocalDateTime timestamp;
}


