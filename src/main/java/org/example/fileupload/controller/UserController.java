//package org.example.fileupload.controller;
//
//
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import lombok.RequiredArgsConstructor;
//import org.example.fileupload.dto.UserCreateRequest;
//import org.example.fileupload.dto.UserProjectsResponse;
//import org.example.fileupload.dto.UserResponse;
//import org.example.fileupload.dto.UserUpdateRequest;
//import org.example.fileupload.repo.UserRepository;
//import org.example.fileupload.service.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
//public class UserController {
//
//    private final UserService userService;
//    private final UserRepository repository;
//
//
//    @PreAuthorize("hasAnyRole( 'USER','ADMIN')")
//    @GetMapping
//    public ResponseEntity<List<UserResponse>> getAllUsers() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//
//    @PreAuthorize("hasRole( 'ADMIN')")
//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
//        return ResponseEntity.ok(userService.getUserById(id));
//    }
//
//
//    @PreAuthorize("hasRole( 'ADMIN')")
//    @PostMapping
//    public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
//        UserResponse response = userService.createUser(request);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//
//    @PreAuthorize("hasRole( 'ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<UserResponse> updateUser(
//            @PathVariable Integer id,
//            @RequestBody UserUpdateRequest request) {
//        return ResponseEntity.ok(userService.updateUser(id, request));
//    }
//
//
//    @PreAuthorize("hasRole( 'ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//
//    @PreAuthorize("hasRole( 'ADMIN')")
//    @GetMapping("/phone/{phoneNumber}")
//    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
//        return ResponseEntity.ok(userService.getUserByEmail(email));
//    }
//
////    @GetMapping("/get_fileByUser/{id}")
////    public ResponseEntity<UserProjectsResponse> getUserProjects(
////            @PathVariable Integer id
////    ) {
////        return ResponseEntity.ok(userService.getUserProjects(id));
////    }
//
//
//}
//
