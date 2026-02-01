//package org.example.fileupload.config;
//
//
//import org.example.fileupload.entity.User;
//import org.example.fileupload.entity.enums.Role;
//import org.example.fileupload.repo.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class DataLoader {
//
//    @Bean
//    CommandLineRunner initUsers(
//            UserRepository userRepository,
//            PasswordEncoder passwordEncoder) {
//
//        return args -> {
//            // Agar bazada hech qanday user bo'lmasa, faqat shunda qo'shamiz
//            if (userRepository.count() == 0) {
//                List<User> users = new ArrayList<>();
//
//                users.add(User.builder()
//                        .fullName("Sherali Admin")
//                        .email("admin@example.com")
//                        .password(passwordEncoder.encode("admin123"))
//                        .role(Role.ADMIN)
//                        .build());
//
//                users.add(User.builder()
//                        .fullName("Ali Valiyev")
//                        .email("ali@example.com")
//                        .password(passwordEncoder.encode("user123"))
//                        .role(Role.USER)
//                        .build());
//
//                users.add(User.builder()
//                        .fullName("Nodira Karimova")
//                        .email("nodira@example.com")
//                        .password(passwordEncoder.encode("user123"))
//                        .role(Role.USER)
//                        .build());
//
//                users.add(User.builder()
//                        .fullName("Javohirbek Saidov")
//                        .email("javohir@example.com")
//                        .password(passwordEncoder.encode("user123"))
//                        .role(Role.USER)
//                        .build());
//
//                users.add(User.builder()
//                        .fullName("Dilnoza Rahmonova")
//                        .email("dilnoza@example.com")
//                        .password(passwordEncoder.encode("user123"))
//                        .role(Role.USER)
//                        .build());
//
//                userRepository.saveAll(users);
//                System.out.println("5 ta test foydalanuvchi muvaffaqiyatli yaratildi!");
//            } else {
//                System.out.println("Bazada allaqachon foydalanuvchilar mavjud, dataloader ishlatilmadi.");
//            }
//        };
//    }
//}