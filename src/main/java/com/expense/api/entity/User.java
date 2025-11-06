package com.expense.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer userId;

    @NonNull
    @NotBlank(message = "User name cannot be empty")
    private String userName;

    @NonNull
    @NotBlank(message = "EmailId cannot be empty")
    private String userEmail;

    @NonNull
    @NotBlank(message = "Password cannot be empty")
    private String userPassword;

    private Role role=Role.USER;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean enabled = true;
    private String oauthId;

}
