package org.example.fileupload.repo;


// UploadedFileRepository.java

import org.example.fileupload.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> {


    List<UploadedFile> findByOwnerId(Integer ownerId);


    @Query("SELECT f FROM UploadedFile f JOIN f.usersWithAccess u WHERE u.id = :userId")
    List<UploadedFile> findAccessibleByUserId(Integer userId);


}