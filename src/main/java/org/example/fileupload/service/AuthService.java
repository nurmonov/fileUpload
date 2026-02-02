package org.example.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.AuthRequest;
import org.example.fileupload.dto.AuthResponse;
import org.example.fileupload.dto.RegisterRequest;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.Role;
import org.example.fileupload.repo.UserRepository;
import org.example.fileupload.securyti.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email yoki parol noto'g'ri"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Email yoki parol noto'g'ri");
        }


        if (!user.isEnabled()) {
            throw new BadCredentialsException("Hisob faol emas");
        }


        String token = jwtUtil.generateToken(user);


        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
    public AuthResponse register(RegisterRequest request) {


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email allaqachon ro'yxatdan o'tgan");
        }


        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)           // default rol
                .build();


        User savedUser = userRepository.save(user);


        String token = jwtUtil.generateToken(savedUser);


        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .build();
    }
}