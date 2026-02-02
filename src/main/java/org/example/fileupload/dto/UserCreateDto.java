package org.example.fileupload.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    private String fullName;
    private String email;

    private String password;

    private String role;
}
