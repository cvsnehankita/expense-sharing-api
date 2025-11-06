package com.expense.api.config;

import com.expense.api.entity.Role;
import com.expense.api.entity.User;
import com.expense.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Create admin user
                User admin = User.builder()
                        .userName("Admin User")
                        .userEmail("admin@example.com")
                        .userPassword(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
                log.info("Admin user created: {}", admin.getUserEmail());

                // Create test users
                User alice = User.builder()
                        .userName("Alice Johnson")
                        .userEmail("alice@example.com")
                        .userPassword(passwordEncoder.encode("alice123"))
                        .role(Role.USER)
                        .enabled(true)
                        .build();
                userRepository.save(alice);
                log.info("Test user created: {}", alice.getUserEmail());

                User bob = User.builder()
                        .userName("Bob Smith")
                        .userEmail("bob@example.com")
                        .userPassword(passwordEncoder.encode("bob123"))
                        .role(Role.USER)
                        .enabled(true)
                        .build();
                userRepository.save(bob);
                log.info("Test user created: {}", bob.getUserEmail());

                User charlie = User.builder()
                        .userName("Charlie Brown")
                        .userEmail("charlie@example.com")
                        .userPassword(passwordEncoder.encode("charlie123"))
                        .role(Role.USER)
                        .enabled(true)
                        .build();
                userRepository.save(charlie);
                log.info("Test user created: {}", charlie.getUserEmail());

                log.info("===================================");
                log.info("Test users loaded successfully!");
                log.info("Admin: admin@example.com / admin123");
                log.info("User1: alice@example.com / alice123");
                log.info("User2: bob@example.com / bob123");
                log.info("User3: charlie@example.com / charlie123");
                log.info("===================================");
            }
        };
    }
}
