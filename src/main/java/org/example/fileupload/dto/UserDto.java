package org.example.fileupload.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String fullName;
    private String email;
    private String role;           // String sifatida, enum'dan o'tkazilgan
}

