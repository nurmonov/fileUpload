package org.example.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.AuthRequest;
import org.example.fileupload.dto.AuthResponse;
import org.example.fileupload.dto.RegisterRequest;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.Role;
import org.example.fileupload.repo.UserRepository;
import org.example.fileupload.securyti.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



// service/AuthService.java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest request) {
        // Email va password bilan autentifikatsiya
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Principaldan User olish
        User user = (User) authentication.getPrincipal();

        // JWT token yaratish
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token);
    }

    // Register (agar kerak bo'lsa, alohida)
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email allaqachon mavjud");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)  // default
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token);
    }
}