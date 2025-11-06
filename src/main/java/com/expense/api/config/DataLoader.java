package com.expense.api.config;

import com.expense.api.entity.Role;
import com.expense.api.entity.User;
import com.expense.api.repository.ExpenseRepository;
import com.expense.api.repository.GroupRepository;
import com.expense.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder, GroupRepository groupRepository, ExpenseRepository expenseRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUserName("admin");
                admin.setUserEmail("admin@example.com");
                admin.setUserPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(true);
                System.out.println("From default loader: uname: " + admin.getUserEmail() + " , " + admin.getUserPassword());
                userRepository.save(admin);

                User user1 = User.builder()
                        .userName("Alice")
                        .userEmail("alice@example.com")
                        .userPassword(passwordEncoder.encode("alice123"))
                        .role(Role.USER)
                        .enabled(true)
                        .build();
                userRepository.save(user1);

                User user2 = User.builder()
                        .userName("Bob")
                        .userEmail("bob@example.com")
                        .userPassword(passwordEncoder.encode("bob123"))
                        .role(Role.USER)
                        .enabled(true)
                        .build();
                userRepository.save(user2);
            }
        };
    }
}
