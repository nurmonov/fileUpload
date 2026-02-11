package org.example.fileupload.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.fileupload.dto.FileMonitoringDto;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.entity.FileActivity;
import org.example.fileupload.entity.UploadedFile;
import org.example.fileupload.entity.User;
import org.example.fileupload.entity.enums.FileAction;
import org.example.fileupload.entity.enums.FileStatus;
import org.example.fileupload.entity.enums.Role;
import org.example.fileupload.mapper.FileActivityMapper;
import org.example.fileupload.mapper.UploadedFileMapper;
import org.example.fileupload.repo.FileActivityRepository;
import org.example.fileupload.repo.UploadedFileRepository;
import org.example.fileupload.repo.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private final FileActivityRepository fileActivityRepository;
    private final FileActivityMapper fileActivityMapper;

    // =========================
    // CURRENT USER
    // =========================
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }

    // =========================
    // UPLOAD MULTIPLE FILES
    // =========================
    @Transactional
    public List<UploadedFileDto> uploadMultipleFiles(
            MultipartFile[] files,
            Integer userId,
            String description,
            List<Integer> sharedUserIds,
            String asos,
            String ishlatilishi,
            String newStatus   // yangi parametr (masalan: "PLANNED", "ONGOING", "COMPLETED")
    ) {

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Hech qanday fayl tanlanmagan");
        }

        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User topilmadi: " + userId));

        List<UploadedFileDto> result = new ArrayList<>();

        String details = "Fayllar yuklandi";
        if (description != null && !description.isBlank()) {
            details += ". Tavsif: " + description;
        }
        if (asos != null && !asos.isBlank()) {
            details += ". Asos: " + asos;
        }
        if (ishlatilishi != null && !ishlatilishi.isBlank()) {
            details += ". Ishlatilishi: " + ishlatilishi;
        }
        // Statusni details ga qo'shamiz (agar kelsa)
        FileStatus statusToSet = FileStatus.REJALASHTIRILGAN; // default
        if (newStatus != null && !newStatus.isBlank()) {
            try {
                statusToSet = FileStatus.valueOf(newStatus.trim().toUpperCase());
                details += ". Status: " + statusToSet.name();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Noto'g'ri status: " + newStatus + ". Mumkin bo'lgan qiymatlar: PLANNED, ONGOING, COMPLETED");
            }
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

                Files.createDirectories(targetPath.getParent());
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                UploadedFile entity = UploadedFile.builder()
                        .originalFileName(originalFilename)
                        .storedFileName(storedFilename)
                        .filePath(targetPath.toString())
                        .contentType(file.getContentType())
                        .fileSize(file.getSize())
                        .uploadDate(LocalDateTime.now())
                        .owner(uploader)
                        .asos(asos)
                        .ishlatilishi(ishlatilishi)
                        .status(statusToSet)          // ← mana shu yerda status qo'yiladi
                        .build();

                // Owner access
                entity.addUser(uploader);

                // UPLOAD activity
                FileActivity uploadActivity = FileActivity.builder()
                        .file(entity)
                        .performedBy(uploader)
                        .action(FileAction.UPLOAD)
                        .details(details)
                        .timestamp(LocalDateTime.now())
                        .build();
                safeAddActivity(entity, uploadActivity);

                // SHARE users
                if (sharedUserIds != null && !sharedUserIds.isEmpty()) {
                    for (Integer sharedId : sharedUserIds) {
                        if (sharedId.equals(userId)) continue;

                        User sharedUser = userRepository.findById(sharedId)
                                .orElseThrow(() -> new RuntimeException("Shared user topilmadi: " + sharedId));

                        entity.addUser(sharedUser);

                        FileActivity shareActivity = FileActivity.builder()
                                .file(entity)
                                .performedBy(sharedUser)
                                .action(FileAction.SHARE)
                                .details("Fayl " + uploader.getEmail() + " tomonidan ulashildi")
                                .timestamp(LocalDateTime.now())
                                .build();
                        safeAddActivity(entity, shareActivity);
                    }
                }

                uploadedFileRepository.save(entity);

                String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/files/download/")
                        .path(entity.getStoredFileName())
                        .toUriString();

                UploadedFileDto dto = uploadedFileMapper.toDto(entity);
                dto.setFileUrl(fileUrl);

                result.add(dto);

            } catch (IOException e) {
                throw new RuntimeException("Fayl saqlashda xato: " + file.getOriginalFilename(), e);
            }
        }

        if (result.isEmpty()) {
            throw new RuntimeException("Hech qanday fayl yuklanmadi");
        }

        return result;
    }

    // =========================
    // UPDATE FILE
    // =========================
    @Transactional
    public UploadedFileDto updateFile(
            Integer fileId,
            MultipartFile newFile,
            String description,
            String asos,
            String ishlatilishi
    ) throws IOException {

        User currentUser = getCurrentUser();

        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi: " + fileId));

        if (!file.isAccessibleBy(currentUser)) {
            throw new RuntimeException("Bu faylni o'zgartirish huquqingiz yo'q");
        }

        String details = "Fayl o'zgartirildi";

        if (description != null && !description.isBlank()) {
            details += ". Tavsif: " + description;
        }
        if (asos != null && !asos.isBlank()) {
            details += ". Asos: " + asos;
            file.setAsos(asos);
        }
        if (ishlatilishi != null && !ishlatilishi.isBlank()) {
            details += ". Ishlatilishi: " + ishlatilishi;
            file.setIshlatilishi(ishlatilishi);
        }

        if (newFile != null && !newFile.isEmpty()) {
            Files.deleteIfExists(Paths.get(file.getFilePath()));

            String originalFilename = newFile.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String storedFilename = UUID.randomUUID() + extension;
            Path targetPath = Paths.get(UPLOAD_DIR).resolve(storedFilename);

            Files.copy(newFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            file.setOriginalFileName(originalFilename);
            file.setStoredFileName(storedFilename);
            file.setFilePath(targetPath.toString());
            file.setContentType(newFile.getContentType());
            file.setFileSize(newFile.getSize());
        }

        file.setUploadDate(LocalDateTime.now());

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

    // =========================
    // GET FILES
    // =========================
    public List<UploadedFileDto> getAllFiles() {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            // Admins uchun ham sort qilish mumkin: uploadedFileRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadDate"));
            return uploadedFileRepository.findAll(Sort.by("status").ascending().and(Sort.by("uploadDate").descending()))
                    .stream().map(uploadedFileMapper::toDto).toList();
        }
        return uploadedFileRepository.findAccessibleByUserIdSorted(currentUser.getId())
                .stream().map(uploadedFileMapper::toDto).toList();
    }



    public UploadedFileDto getFileById(Integer fileId) {
        User currentUser = getCurrentUser();

        UploadedFile file = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi"));

        if (!file.isAccessibleBy(currentUser)) {
            throw new RuntimeException("Bu faylni ko‘rish huquqingiz yo‘q");
        }

        return uploadedFileMapper.toDto(file);
    }

    public List<FileMonitoringDto> getMonitoring() {
        User currentUser = getCurrentUser();
        List<UploadedFile> files = (currentUser.getRole() == Role.ADMIN)
                ? uploadedFileRepository.findAll()
                : uploadedFileRepository.findAccessibleByUserIdSorted(currentUser.getId());
        return files.stream().map(file -> {
            List<FileActivity> activities = fileActivityRepository.findByFileIdOrderByTimestampDesc(file.getId()).subList(0, Math.min(5, file.getActivities().size()));
            return FileMonitoringDto.builder()
                    .file(uploadedFileMapper.toDto(file))
                    .recentActivities(activities.stream().map(fileActivityMapper::toDto).toList())
                    .currentStatus(file.getStatus().name())
                    .build();
        }).toList();
    }

    public ByteArrayInputStream exportToExcel() throws IOException {
        // Masalan, barcha fayllarni olish (yoki faqat joriy user uchun)
        User currentUser = getCurrentUser();
        List<UploadedFile> files;
        if (currentUser.getRole() == Role.ADMIN) {
            files = uploadedFileRepository.findAll();
        } else {
            files = uploadedFileRepository.findAccessibleByUserId(currentUser.getId());
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Fayllar ro'yxati");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Fayl nomi", "Status", "Yuklangan sana", "Hajmi (bayt)", "Egasi", "Asos", "Ishlatilishi"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Data rows
            int rowNum = 1;
            for (UploadedFile file : files) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(file.getId());
                row.createCell(1).setCellValue(file.getOriginalFileName());
                row.createCell(2).setCellValue(file.getStatus() != null ? file.getStatus().name() : "Noma'lum");
                row.createCell(3).setCellValue(file.getUploadDate() != null ? file.getUploadDate().toString() : "");
                row.createCell(4).setCellValue(file.getFileSize() != null ? file.getFileSize() : 0);
                row.createCell(5).setCellValue(file.getOwner() != null ? file.getOwner().getFullName() : "Noma'lum");
                row.createCell(6).setCellValue(file.getAsos() != null ? file.getAsos() : "");
                row.createCell(7).setCellValue(file.getIshlatilishi() != null ? file.getIshlatilishi() : "");
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // HELPERS
    // =========================
        private void safeAddActivity(UploadedFile entity, FileActivity activity) {
            if (entity.getActivities() == null) {
                entity.setActivities(new ArrayList<>());
            }
            entity.getActivities().add(activity);
        }
}
