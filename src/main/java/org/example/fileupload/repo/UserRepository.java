package org.example.fileupload.repo;



// UserRepository.java

import org.example.fileupload.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Spring Security uchun - email orqali topish (JWT authenticationda kerak)
    Optional<User> findByEmail(String email);

    // Kabinet uchun - foydalanuvchining owned files va accessible files ni yuklash mumkin, lekin entity'da bor
    // Qo'shimcha metodlar: masalan, admin uchun all users
}

