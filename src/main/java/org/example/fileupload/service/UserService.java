package org.example.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.UserCreateDto;
import org.example.fileupload.dto.UserDto;
import org.example.fileupload.dto.UserSummaryDto;
import org.example.fileupload.dto.UserUpdateDto;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.Role;
import org.example.fileupload.mapper.UserMapper;
import org.example.fileupload.repo.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // Create (yangi user yaratish)
    public UserDto createUser(UserCreateDto dto) {
        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.valueOf(dto.getRole()))  // enumdan keladi
                .build();

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    // Read - bitta user (to‘liq ma’lumot)
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User topilmadi: " + id));
        return userMapper.toDto(user);
    }

    // Read - qisqa ma’lumot (summary)
    public UserSummaryDto getUserSummaryById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User topilmadi: " + id));
        return userMapper.toSummaryDto(user);
    }

    // Read - barcha userlar (faqat summary)
    public List<UserSummaryDto> getAllUsersSummary() {
        return userRepository.findAll().stream()
                .map(userMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    // Update
    public UserDto updateUser(Integer id, UserUpdateDto dto, UserDetails currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User topilmadi: " + id));

        // Faqat o'zini yangilashi mumkin (email bilan solishtiramiz)
        if (!user.getEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("Faqat o'zingizni yangilashingiz mumkin");
        }

        // Yangilash
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        // Role ni o'zgartirish faqat admin uchun bo'lishi mumkin (hozircha blok qilamiz)
        // if (dto.getRole() != null) user.setRole(dto.getRole());

        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    // Delete
    public void deleteUser(Integer id, UserDetails currentUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User topilmadi: " + id));

        // Faqat o'zini o'chirishi mumkin
//        if (!user.getEmail().equals(currentUser.getUsername())) {
//            throw new AccessDeniedException("Faqat o'zingizni o'chirishingiz mumkin");
//        }

        userRepository.delete(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User topilmadi: " + email));
    }
}