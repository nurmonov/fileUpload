package org.example.fileupload.entity;

// FileActivity.java  (log / description uchun)

import jakarta.persistence.*;
import lombok.*;
import org.example.fileupload.entity.enums.FileAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private UploadedFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileAction action;

    private String details;                    // masalan: "Fayl nomi o'zgartirildi", "2-bob qo'shildi"

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
