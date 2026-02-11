package org.example.fileupload.repo;


// UploadedFileRepository.java

import org.example.fileupload.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Integer> , JpaSpecificationExecutor<UploadedFile> {


    List<UploadedFile> findByOwnerId(Integer ownerId);


    @Query("SELECT f FROM UploadedFile f JOIN f.usersWithAccess u WHERE u.id = :userId")
    List<UploadedFile> findAccessibleByUserId(Integer userId);


    @Query("SELECT f FROM UploadedFile f WHERE f.owner.id = :userId OR :userId IN (SELECT u.id FROM f.usersWithAccess u) ORDER BY CASE WHEN f.status = 'COMPLETED' THEN 1 WHEN f.status = 'ONGOING' THEN 2 ELSE 3 END, f.uploadDate DESC")
    List<UploadedFile> findAccessibleByUserIdSorted(Integer userId);
}