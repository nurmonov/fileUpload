package org.example.fileupload.repo;

// FileActivityRepository.java

import org.example.fileupload.entity.FileActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileActivityRepository extends JpaRepository<FileActivity, Integer> {


    List<FileActivity> findByFileIdOrderByTimestampDesc(Long fileId);


    List<FileActivity> findByPerformedByIdOrderByTimestampDesc(Integer userId);
}