package org.example.fileupload.repo;


// UploadedFileRepository.java

import org.example.fileupload.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {

    // Foydalanuvchi uchun o'z fayllarini topish (owner sifatida)
    List<UploadedFile> findByOwnerId(Integer ownerId);

    // Foydalanuvchi uchun accessible fayllarni topish (ManyToMany orqali)
    @Query("SELECT f FROM UploadedFile f JOIN f.usersWithAccess u WHERE u.id = :userId")
    List<UploadedFile> findAccessibleByUserId(Integer userId);

    // Admin uchun barcha fayllar - findAll() yetarli, lekin qo'shimcha filterlar mumkin
}
