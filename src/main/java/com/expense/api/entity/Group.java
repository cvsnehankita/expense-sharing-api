package com.expense.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Group name cannot be empty")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    @JsonIgnore
    private Set<User> members = new HashSet<>();
    private LocalDateTime createdAt = LocalDateTime.now();

    @Version
    private Long version;  // For optimistic locking

    public void addUser(User user) {
        if (this.members == null) {
            this.members = new HashSet<>();
        }
        this.members.add(user);
    }

    public void removeUser(User user) {
        if (this.members != null) {
            this.members.remove(user);
        }
    }

    public boolean isMember(User user) {
        return this.members != null && this.members.contains(user);
    }
}
