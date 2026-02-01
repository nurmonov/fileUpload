package org.example.fileupload.repo;

// FileActivityRepository.java

import org.example.fileupload.entity.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity, Integer> {

    // Fayl uchun barcha activities ni topish (timeline uchun)
    List<FileActivity> findByFileIdOrderByTimestampDesc(Long fileId);

    // Foydalanuvchi uchun o'z harakatlarini topish (ixtiyoriy, admin uchun)
    List<FileActivity> findByPerformedByIdOrderByTimestampDesc(Integer userId);
}
