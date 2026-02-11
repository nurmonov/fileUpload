package org.example.fileupload.controller;

import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.FileMonitoringDto;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.UploadedFile;
import org.example.fileupload.entity.User;
import org.example.fileupload.mapper.UploadedFileMapper;
import org.example.fileupload.repo.UploadedFileRepository;
import org.example.fileupload.repo.UserRepository;
import org.example.fileupload.service.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
    public ResponseEntity<List<UploadedFileDto>> uploadMultipleFiles(
            @RequestPart("files") MultipartFile[] files,

            @RequestPart(value = "userId", required = true) String userIdStr,

            @RequestPart(value = "description", required = false) String description,

            @RequestPart(value = "sharedUserIds", required = false) String sharedUserIdsJson,

            @RequestPart(value = "asos", required = false) String asos,

            @RequestPart(value = "ishlatilishi", required = false) String ishlatilishi,

            @RequestPart(value = "status", required = false) String status   // yangi: PLANNED, ONGOING, COMPLETED
    ) {
        // userId ni parse qilish
        Integer userId;
        try {
            userId = Integer.parseInt(userIdStr.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(null); // yoki custom error response
        }

        // sharedUserIds ni parse qilish ("[1,2,3]" yoki "1,2,3" formatida kelishi mumkin)
        List<Integer> sharedUserIds = new ArrayList<>();
        if (sharedUserIdsJson != null && !sharedUserIdsJson.isBlank()) {
            try {
                String cleaned = sharedUserIdsJson
                        .replace("[", "")
                        .replace("]", "")
                        .replace("\"", "")
                        .replace(" ", "");

                if (!cleaned.isEmpty()) {
                    sharedUserIds = Arrays.stream(cleaned.split(","))
                            .filter(s -> !s.isBlank())
                            .map(String::trim)
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest()
                        .body(null); // yoki error message qaytarish yaxshiroq
            }
        }

        // Service chaqirish
        List<UploadedFileDto> result = fileService.uploadMultipleFiles(
                files,
                userId,
                description,
                sharedUserIds,
                asos,
                ishlatilishi,
                status  // statusni ham uzatamiz
        );

        return ResponseEntity.ok(result);
    }

    // ────────────────────────────────────────────────
    // Monitoring endpoint (fayllar holati va faoliyatini ko'rish)
    // ────────────────────────────────────────────────

    /**
     * Monitoring - joriy foydalanuvchi uchun mavjud fayllar va ularning holati/activities
     * Admin bo'lsa - barcha fayllar
     */
    @GetMapping("/monitoring")
    public ResponseEntity<List<FileMonitoringDto>> getFileMonitoring() {
        List<FileMonitoringDto> monitoringData = fileService.getMonitoring();
        return ResponseEntity.ok(monitoringData);
    }

    /**
     * Excel export (masalan, barcha fayllar ro'yxatini)
     */


    @PutMapping(value = "/{fileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadedFileDto> updateFile(
            @PathVariable Integer fileId,

            @RequestPart(value = "file", required = false)
            MultipartFile newFile,   // yangi fayl (ixtiyoriy)

            @RequestPart(value = "description", required = false)
            String description,

            @RequestPart(value = "asos", required = false)
            String asos,

            @RequestPart(value = "ishlatilishi", required = false)
            String ishlatilishi
    ) throws IOException {

        UploadedFileDto updatedFile = fileService.updateFile(
                fileId,
                newFile,
                description,
                asos,
                ishlatilishi
        );

        return ResponseEntity.ok(updatedFile);
    }


    @GetMapping
    public ResponseEntity<List<UploadedFileDto>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }


    @GetMapping("/{fileId}")
    public ResponseEntity<UploadedFileDto> getFileById(@PathVariable Integer fileId) {
        return ResponseEntity.ok(fileService.getFileById(fileId));
    }


    @GetMapping("/my-files")
    public ResponseEntity<List<UploadedFileDto>> getMyFiles() {
        User currentUser = getCurrentUser();  // yoki userId orqali
        List<UploadedFileDto> myFiles = uploadedFileRepository.findAccessibleByUserId(currentUser.getId())
                .stream()
                .map(uploadedFileMapper::toDto)
                .toList();
        return ResponseEntity.ok(myFiles);
    }

    @GetMapping("/monitoring/export")
    public ResponseEntity<byte[]> exportMonitoringToExcel() throws IOException {
        // Service dan stream olish
        ByteArrayInputStream in = fileService.exportToExcel();

        // byte[] ga aylantirish
        byte[] bytes = in.readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fayllar_monitoring.xlsx");
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }


    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }



}