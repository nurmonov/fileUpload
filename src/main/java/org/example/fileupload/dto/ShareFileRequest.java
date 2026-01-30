package org.example.fileupload.dto;


import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareFileRequest {

    private Long fileId;
    private Set<Integer> userIds;   // ulashmoqchi bo'lgan userlarning ID'lari
}
