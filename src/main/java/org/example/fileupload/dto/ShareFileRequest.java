package org.example.fileupload.dto;


import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareFileRequest {

    private Integer fileId;
    private List<Integer> userIds;
}
