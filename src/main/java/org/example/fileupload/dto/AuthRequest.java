package org.example.fileupload.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
    private String fullName;  // Register uchun
}



