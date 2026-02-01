package org.example.fileupload.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.FileActivity;
import org.example.fileupload.entity.UploadedFile;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.FileAction;
import org.example.fileupload.entity.enums.Role;
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


    // Current user ni JWT orqali olish
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }

    @Transactional
    public List<UploadedFileDto> uploadMultipleFiles(
            MultipartFile[] files,
            Integer userId,                     // yangi parametr
            String description,
            List<Integer> sharedUserIds
    ) {
        List<UploadedFileDto> result = new ArrayList<>();

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Hech qanday fayl tanlanmagan");
        }

        // User ni ID orqali topamiz (requestdan kelgan)
        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Berilgan userId topilmadi: " + userId));

        String details = "Fayllar yuklandi";
        if (description != null && !description.isBlank()) {
            details += ". Tavsif: " + description;
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            try {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String storedFilename = UUID.randomUUID() + extension;
                Path targetPath = Paths.get(UPLOAD_DIR).resolve(storedFilename);

                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                UploadedFile entity = UploadedFile.builder()
                        .originalFileName(originalFilename)
                        .storedFileName(storedFilename)
                        .filePath(targetPath.toString())
                        .contentType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadDate(LocalDateTime.now())
                        .owner(uploader)               // ← uploader = requestdagi userId
                        .build();

                // Owner'ni access ro'yxatiga qo'shamiz
                entity.addUser(uploader);

                // Activity log (uploader nomi bilan)
                FileActivity activity = FileActivity.builder()
                        .file(entity)
                        .performedBy(uploader)
                        .action(FileAction.UPLOAD)
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build();

                entity.getActivities().add(activity);

                // Qo'shimcha userlarni ulashish (sharedUserIds)
                if (sharedUserIds != null && !sharedUserIds.isEmpty()) {
                    for (Integer sharedId : sharedUserIds) {
                        if (sharedId.equals(userId)) continue; // o'zini qayta qo'shmaymiz

                        User sharedUser = userRepository.findById(sharedId)
                                .orElseThrow(() -> new IllegalArgumentException("Ulashiladigan user topilmadi: " + sharedId));

                        entity.addUser(sharedUser);

                        FileActivity shareAct = FileActivity.builder()
                                .file(entity)
                                .performedBy(uploader)
                                .action(FileAction.SHARE)
                                .details("Ulashildi: " + sharedUser.getEmail() + " (yuklash vaqtida)")
                                .timestamp(LocalDateTime.now())
                                .build();

                        entity.getActivities().add(shareAct);
                    }
                }

                uploadedFileRepository.save(entity);

                UploadedFileDto dto = uploadedFileMapper.toDto(entity);
                dto.setFileUrl("/api/files/download/" + entity.getStoredFileName());

                result.add(dto);

            } catch (IOException e) {
                System.err.println("Fayl saqlashda xato: " + file.getOriginalFilename() + " → " + e.getMessage());
            }
        }

        if (result.isEmpty()) {
            throw new RuntimeException("Hech qanday fayl muvaffaqiyatli yuklanmadi");
        }

        return result;
    }

    // FileService ichiga quyidagi metodlarni qo'shing (oldingi kod bilan birga)

    @Transactional
    public UploadedFileDto updateFile(Integer fileId, MultipartFile newFile, String description) throws IOException {
        User currentUser = getCurrentUser();  // yoki userId orqali, sizning variantingizga qarab

        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi: " + fileId));

        // Huquq tekshirish: faqat owner yoki access bor user o'zgartira oladi
        if (!file.isAccessibleBy(currentUser)) {
            throw new RuntimeException("Bu faylni o'zgartirish huquqingiz yo'q");
        }

        String details = "Fayl o'zgartirildi";
        if (description != null && !description.isBlank()) {
            details += ". Yangi tavsif: " + description;
        }

        // Agar yangi fayl yuborilgan bo'lsa — eski faylni o'chirib, yangisini saqlaymiz
        if (newFile != null && !newFile.isEmpty()) {
            // Eski faylni diskdan o'chirish (ixtiyoriy, lekin tavsiya etiladi)
            Path oldPath = Paths.get(file.getFilePath());
            Files.deleteIfExists(oldPath);

            // Yangi faylni saqlash
            String originalFilename = newFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String storedFilename = UUID.randomUUID() + extension;
            Path targetPath = Paths.get(UPLOAD_DIR).resolve(storedFilename);

            Files.copy(newFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Entity ni yangilash
            file.setOriginalFileName(originalFilename);
            file.setStoredFileName(storedFilename);
            file.setFilePath(targetPath.toString());
            file.setContentType(newFile.getContentType());
            file.setFileSize(newFile.getSize());
        }

        file.setUploadDate(LocalDateTime.now());  // oxirgi o'zgarish vaqti

        // Activity log
        FileActivity activity = FileActivity.builder()
                .file(file)
                .performedBy(currentUser)
                .action(FileAction.UPDATE)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        file.getActivities().add(activity);

        uploadedFileRepository.save(file);

        UploadedFileDto dto = uploadedFileMapper.toDto(file);
        dto.setFileUrl("/api/files/download/" + file.getStoredFileName());

        return dto;
    }

    public List<UploadedFileDto> getAllFiles() {
        User currentUser = getCurrentUser();

        // Agar admin bo'lsa — hammasini qaytaradi
        if (currentUser.getRole() == Role.ADMIN) {
            return uploadedFileRepository.findAll().stream()
                    .map(uploadedFileMapper::toDto)
                    .toList();
        }

        // Oddiy user uchun faqat o'ziga tegishli (owned + accessible)
        return uploadedFileRepository.findAccessibleByUserId(currentUser.getId()).stream()
                .map(uploadedFileMapper::toDto)
                .toList();
    }

    public UploadedFileDto getFileById(Integer fileId) {
        User currentUser = getCurrentUser();

        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi: " + fileId));

        if (!file.isAccessibleBy(currentUser)) {
            throw new RuntimeException("Bu faylni ko'rish huquqingiz yo'q");
        }

        return uploadedFileMapper.toDto(file);
    }
}