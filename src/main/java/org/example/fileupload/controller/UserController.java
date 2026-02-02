package org.example.fileupload.controller;

import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.*;
import org.example.fileupload.entity.User;
import org.example.fileupload.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Yangi user yaratish (admin yoki test uchun)
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserCreateDto dto) {
        UserDto created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Bitta user ma’lumotini olish (to‘liq)
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Qisqa ma’lumot (summary)
    @GetMapping("/{id}/summary")
    public ResponseEntity<UserSummaryDto> getSummaryById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserSummaryById(id));
    }

    // Barcha userlar (faqat summary)
    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllSummary() {
        return ResponseEntity.ok(userService.getAllUsersSummary());
    }

    // O‘z profilini yangilash
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Integer id,
            @RequestBody UserUpdateDto dto,
            Authentication authentication
    ) {
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        UserDto updated = userService.updateUser(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    // O‘z profilini o‘chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        userService.deleteUser(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // O‘z profilini ko‘rish (qisqa)
    @GetMapping("/me")
    public ResponseEntity<UserSummaryDto> getMyProfile(Authentication authentication) {
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(currentUser.getUsername()); // yordamchi metod
        return ResponseEntity.ok(userService.getUserSummaryById(user.getId()));
    }
}