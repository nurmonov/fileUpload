package org.example.fileupload.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileRequest {

    private String originalFileName;   // MultipartFile'dan olinadi, lekin metadata uchun
    // contentType va fileSize ham MultipartFile'dan olinadi
    // filePath yoki data ni service'da boshqaramiz
}
