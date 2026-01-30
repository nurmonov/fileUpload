package org.example.fileupload.controller;

import org.example.fileupload.dto.CreateFileRequest;
import org.example.fileupload.dto.FileActivityDto;
import org.example.fileupload.dto.FileDetailDto;
import org.example.fileupload.dto.ShareFileRequest;
import org.example.fileupload.dto.UploadedFileDto;
import org.example.fileupload.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadedFileDto> upload(@RequestParam("file") MultipartFile file,
                                                  CreateFileRequest request) throws IOException {
        return ResponseEntity.ok(fileService.uploadFile(file, request));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<Void> share(@PathVariable Long id, @RequestBody ShareFileRequest request) {
        request.setFileId(id);
        fileService.shareFile(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-files")
    public ResponseEntity<List<UploadedFileDto>> getMyFiles() {
        return ResponseEntity.ok(fileService.getMyFiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDetailDto> getFileDetail(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileDetail(id));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<FileActivityDto>> getActivities(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileActivities(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UploadedFileDto>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }
}