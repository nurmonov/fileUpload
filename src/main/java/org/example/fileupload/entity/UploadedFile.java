package org.example.fileupload.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "uploaded_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"owner", "usersWithAccess", "activities"})
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "file_user_access",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default                  // ← bu allaqachon qo'shilgan bo'lsa ham qoldiring
    private Set<User> usersWithAccess = new HashSet<>();

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    @Builder.Default                  // ← BU YERGA HAM QO'SHING!
    private List<FileActivity> activities = new ArrayList<>();

    // Qo'shimcha metodlar (agar bor bo'lsa)
    public void addUser(User user) {
        if (user != null) {
            this.usersWithAccess.add(user);
        }
    }

    public boolean isOwner(User user) {
        return user != null && this.owner != null && this.owner.equals(user);
    }

    public boolean isAccessibleBy(User user) {
        return user != null && (isOwner(user) || this.usersWithAccess.contains(user));
    }
}