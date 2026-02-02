package org.example.fileupload.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareFileRequest {

    private Integer fileId;
    private List<Integer> userIds;
}
