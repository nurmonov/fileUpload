package org.example.fileupload.dto;


import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFileRequest {

    private String originalFileName;
}
