package org.example.fileupload.controller;

import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.CreateFileRequest;
import org.example.fileupload.dto.FileActivityDto;
import org.example.fileupload.dto.FileDetailDto;
import org.example.fileupload.dto.ShareFileRequest;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.User;
import org.example.fileupload.mapper.UploadedFileMapper;
import org.example.fileupload.repo.UploadedFileRepository;
import org.example.fileupload.repo.UserRepository;
import org.example.fileupload.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserRepository userRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileMapper  uploadedFileMapper;




    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadedFileDto>> uploadMultiple(
            @RequestPart("files") MultipartFile[] files,
            @RequestPart(value = "userId") String userIdStr,          // String qilib oling
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "sharedUserIds", required = false) String sharedUserIdsStr  // JSON string sifatida
    ) {
        Integer userId = Integer.parseInt(userIdStr.trim());

        List<Integer> sharedUserIds = new ArrayList<>();
        if (sharedUserIdsStr != null && !sharedUserIdsStr.isBlank()) {
            // "[2,3,5]" → List<Integer>
            String cleaned = sharedUserIdsStr.replace("[", "").replace("]", "").replace("\"", "");
            sharedUserIds = Arrays.stream(cleaned.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        // keyin service chaqirish
        return ResponseEntity.ok(fileService.uploadMultipleFiles(files, userId, description, sharedUserIds));
    }

    // FileController ichiga quyidagilarni qo'shing

    // Faylni o'zgartirish (PUT yoki PATCH ishlatish mumkin, lekin PUT ko'proq qo'llaniladi)
    @PutMapping(value = "/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadedFileDto> updateFile(
            @PathVariable Integer fileId,
            @RequestPart(value = "file", required = false) MultipartFile newFile,      // yangi fayl ixtiyoriy
            @RequestPart(value = "description", required = false) String description) throws IOException {

        return ResponseEntity.ok(fileService.updateFile(fileId, newFile, description));
    }

    // Barcha fayllarni olish (my-files yoki all-files)
    @GetMapping
    public ResponseEntity<List<UploadedFileDto>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }

    // Bitta faylni ID bo'yicha olish
    @GetMapping("/{fileId}")
    public ResponseEntity<UploadedFileDto> getFileById(@PathVariable Integer fileId) {
        return ResponseEntity.ok(fileService.getFileById(fileId));
    }

    // Agar faqat o'z fayllarini ko'rishni alohida endpoint qilmoqchi bo'lsangiz:
    @GetMapping("/my-files")
    public ResponseEntity<List<UploadedFileDto>> getMyFiles() {
        User currentUser = getCurrentUser();  // yoki userId orqali
        List<UploadedFileDto> myFiles = uploadedFileRepository.findAccessibleByUserId(currentUser.getId())
                .stream()
                .map(uploadedFileMapper::toDto)
                .toList();
        return ResponseEntity.ok(myFiles);
    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }


//    @PostMapping("/upload")
//    public ResponseEntity<UploadedFileDto> upload(@RequestParam("file") MultipartFile file,
//                                                  CreateFileRequest request) throws IOException {
//        return ResponseEntity.ok(fileService.uploadFile(file, request));
//    }
//
//    @PostMapping("/{id}/share")
//    public ResponseEntity<Void> share(@PathVariable Long id, @RequestBody ShareFileRequest request) {
//        request.setFileId(id);
//        fileService.shareFile(request);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/my-files")
//    public ResponseEntity<List<UploadedFileDto>> getMyFiles() {
//        return ResponseEntity.ok(fileService.getMyFiles());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<FileDetailDto> getFileDetail(@PathVariable Long id) {
//        return ResponseEntity.ok(fileService.getFileDetail(id));
//    }
//
//    @GetMapping("/{id}/activities")
//    public ResponseEntity<List<FileActivityDto>> getActivities(@PathVariable Long id) {
//        return ResponseEntity.ok(fileService.getFileActivities(id));
//    }
//
//    @GetMapping("/all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UploadedFileDto>> getAllFiles() {
//        return ResponseEntity.ok(fileService.getAllFiles());
//    }
}