package org.example.fileupload.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
}



