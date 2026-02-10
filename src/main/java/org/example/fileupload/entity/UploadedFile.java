package org.example.fileupload.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"owner", "usersWithAccess", "activities"})
@EqualsAndHashCode(exclude = {"owner", "usersWithAccess", "activities"})
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private LocalDateTime uploadDate;
    private String ishlatilishi;
    private String asos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference("user-owned-files")
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "file_user_access",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonManagedReference("user-accessible-files")
    private Set<User> usersWithAccess = new HashSet<>();

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    @JsonBackReference("file-activities")
    private List<FileActivity> activities = new ArrayList<>();

    public void addUser(User user) {
        if (user == null) {
            return;  // yoki throw qilish mumkin, lekin ko‘pincha return yetarli
        }

        if (this.usersWithAccess == null) {
            this.usersWithAccess = new HashSet<>();
        }

        this.usersWithAccess.add(user);
    }
    public void addActivity(FileActivity activity) {
        if (activity == null) {
            return;
        }
        if (this.activities == null) {
            this.activities = new ArrayList<>();
        }
        this.activities.add(activity);
        activity.setFile(this);
    }

    public boolean isOwner(User user) {
        return user != null && this.owner != null && this.owner.equals(user);
    }

    public boolean isAccessibleBy(User user) {
        return user != null && (isOwner(user) || this.usersWithAccess.contains(user));
    }
}