//package org.example.fileupload.service;
//
//
//
//
//import lombok.RequiredArgsConstructor;
//import org.example.fileupload.dto.UserCreateRequest;
//import org.example.fileupload.dto.UserResponse;
//import org.example.fileupload.dto.UserUpdateRequest;
//import org.example.fileupload.entity.User;
//import org.example.fileupload.entity.enums.Role;
//import org.example.fileupload.mapper.UserMapper;
//import org.example.fileupload.repo.UserRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder;
////    private final UploadedFileMapper uploadedFileMapper;
//    private final FileService fileService;
//
//    @Transactional(readOnly = true)
//    public List<UserResponse> getAllUsers() {
//        return userMapper.toResponseList(userRepository.findAll());
//    }
//
//    @Transactional(readOnly = true)
//    public UserResponse getUserById(Integer id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("Foydalanuvchi topilmadi: " + id));
//        return userMapper.toResponse(user);
//    }
//
//    @Transactional
//    public UserResponse createUser(UserCreateRequest request) {
//        if (userRepository.existsByEmail(request.getPhoneNumber())) {
//            throw new IllegalArgumentException("Bu email  allaqachon ro'yxatdan o'tgan");
//        }
//
//        User user = userMapper.toEntity(request);
//
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//
//        if (user.getRole() == null) {
//            user.setRole(Role.USER);
//        }
//
//        User saved = userRepository.save(user);
//        return userMapper.toResponse(saved);
//    }
//
//    @Transactional
//    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("Foydalanuvchi topilmadi: " + id));
//
//
//        if (request.getFullName() != null && !request.getFullName().isBlank()) {
//            user.setFullName(request.getFullName());
//        }
//
//
//        if (request.getEmail() != null && !request.getEmail().isBlank()) {
//            if (!request.getEmail().equals(user.getEmail()) &&
//                    userRepository.existsByEmail(request.getEmail())) {
//                throw new IllegalArgumentException("Bu email allaqachon ishlatilmoqda");
//            }
//            user.setEmail(request.getEmail());
//        }
//
//
//
//
//        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
//            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        }
//
//
//
//        User updated = userRepository.save(user);
//        return userMapper.toResponse(updated);
//    }
//
//    @Transactional
//    public void deleteUser(Integer id) {
//        if (!userRepository.existsById(id)) {
//            throw new NoSuchElementException("Foydalanuvchi topilmadi: " + id);
//        }
//        userRepository.deleteById(id);
//    }
//
//
//    @Transactional(readOnly = true)
//    public UserResponse getUserByEmail(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NoSuchElementException("Foydalanuvchi topilmadi: " + email));
//        return userMapper.toResponse(user);
//    }
//
////    @Transactional(readOnly = true)
////    public UserProjectsResponse getUserProjects(Integer id) {
////
////        User user = userRepository.findByIdWithFiles(id)
////                .orElseThrow(() -> new RuntimeException("User topilmadi"));
////
////        return UserProjectsResponse.builder()
////                .userId(user.getId())
////                .fullName(user.getFullName())
////                .projects(fileService.toDtoList(user.getUploadedFiles()))
////                .build();
////    }
//
//
//
//}
