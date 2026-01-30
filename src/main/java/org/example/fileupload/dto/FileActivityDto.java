package org.example.fileupload.dto;



import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileActivityDto {

    private Long id;
    private UserSummaryDto performedBy;
    private String action;               // enum string sifatida
    private String details;
    private LocalDateTime timestamp;
}


