package org.example.fileupload.dto;


import lombok.Data;

@Data
public class AuthRequest {
    private String email;      // login uchun asosiy
    private String password;   // parol
}