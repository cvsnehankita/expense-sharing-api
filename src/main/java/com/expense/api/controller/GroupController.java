package com.expense.api.controller;

import com.expense.api.entity.Group;
import com.expense.api.entity.User;
import com.expense.api.service.GroupService;
import com.expense.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createGroup(
            @RequestParam String name,
            @RequestParam List<Integer> userIds,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.createGroup(name, userIds, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping
    public ResponseEntity<List<Group>> getUserGroups(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Group> groups = groupService.getUserGroups(currentUser.getUserId());
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.getGroupById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Check if user is a member of the group
        if (!group.isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not a member of this group"));
        }

        return ResponseEntity.ok(group);
    }
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> addMember(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Only group members can add new members
        if (!group.isMember(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You are not a member of this group"));
        }

        groupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok(Map.of("message", "User added to group successfully"));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMember(
            @PathVariable Integer groupId,
            @PathVariable Integer userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userService.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = groupService.getGroupById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Only the user themselves or an admin can remove a member
        if (!currentUser.getUserId().equals(userId) && !currentUser.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You cannot remove other users from the group"));
        }

        groupService.removeMemberFromGroup(groupId, userId);
        return ResponseEntity.ok(Map.of("message", "User removed from group successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGroup(@PathVariable Integer id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok(Map.of("message", "Group deleted successfully"));
    }
}
