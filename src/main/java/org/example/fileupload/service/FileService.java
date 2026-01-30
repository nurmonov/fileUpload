package org.example.fileupload.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.FileActivity;
import org.example.fileupload.entity.UploadedFile;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.FileAction;
import org.example.fileupload.mapper.UploadedFileMapper;
import org.example.fileupload.repo.UploadedFileRepository;
import org.example.fileupload.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String UPLOAD_DIR = "uploads/";

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;
    private final UploadedFileMapper uploadedFileMapper;

    // Konstruktor orqali papka yaratish (sizning kodingizdan olingan)
    public FileService(UploadedFileRepository uploadedFileRepository,
                       UserRepository userRepository,
                       UploadedFileMapper uploadedFileMapper) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
        this.uploadedFileMapper = uploadedFileMapper;

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Uploads papkasini yaratib bo'lmadi", e);
        }
    }

    // Current user ni JWT orqali olish
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }

    @Transactional
    public List<UploadedFileDto> uploadMultipleFiles(MultipartFile[] files) {
        List<UploadedFileDto> result = new ArrayList<>();

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Hech qanday fayl tanlanmagan");
        }

        User currentUser = getCurrentUser();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String storedFilename = UUID.randomUUID() + extension;
                Path targetPath = Paths.get(UPLOAD_DIR).resolve(storedFilename);

                // Faylni diskka saqlash (sizning uslubingiz)
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Entity yaratish
                UploadedFile entity = UploadedFile.builder()
                        .originalFileName(originalFilename)
                        .storedFileName(storedFilename)
                        .filePath(targetPath.toString())
                        .contentType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadDate(LocalDateTime.now())
                        .owner(currentUser)
                        .build();

                // Owner'ni avtomatik ravishda access ro'yxatiga qo'shish
                entity.addUser(currentUser);

                // Activity log qo'shish
                FileActivity activity = FileActivity.builder()
                        .file(entity)
                        .performedBy(currentUser)
                        .action(FileAction.UPLOAD)
                        .details("Fayl yuklandi: " + originalFilename)
                        .timestamp(LocalDateTime.now())
                        .build();

                entity.getActivities().add(activity);

                // Saqlash
                uploadedFileRepository.save(entity);

                // DTO yaratish (sizning kodingizdagi kabi + fileUrl)
                UploadedFileDto dto = uploadedFileMapper.toDto(entity);
                // Agar mapperda fileUrl bo'lmasa, qo'lda qo'shish mumkin
                dto.setFileUrl("/api/files/download/" + entity.getStoredFileName());

                result.add(dto);

            } catch (IOException e) {
                System.err.println("Fayl saqlashda xato: " + file.getOriginalFilename() + " → " + e.getMessage());
                // xohlasangiz throw qilish mumkin, lekin hozircha davom etamiz
            }
        }

        if (result.isEmpty()) {
            throw new RuntimeException("Hech qanday fayl muvaffaqiyatli yuklanmadi");
        }

        return result;
    }
}