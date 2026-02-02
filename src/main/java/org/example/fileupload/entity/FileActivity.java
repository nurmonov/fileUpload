package org.example.fileupload.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.example.fileupload.entity.enums.FileAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_activities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"file", "performedBy"})
@EqualsAndHashCode(exclude = {"file", "performedBy"})
public class FileActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @JsonManagedReference("file-activities")
    private UploadedFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-activities")
    private User performedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileAction action;

    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}