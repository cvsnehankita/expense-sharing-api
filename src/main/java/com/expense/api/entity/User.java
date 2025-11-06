package com.expense.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NonNull
    @NotBlank(message = "User name cannot be empty")
    private String userName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String userEmail;

    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean enabled = true;

    @Column(unique = true)
    private String oauthId;

    private String oauthProvider;

    @Version
    private Long version;

}
