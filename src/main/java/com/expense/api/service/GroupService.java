package com.expense.api.service;

import com.expense.api.entity.Group;
import com.expense.api.entity.User;
import com.expense.api.repository.GroupRepository;
import com.expense.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public Group createGroup(String groupName, List<Integer> userIds, User creator) {
        Group group = Group.builder()
                .name(groupName)
                .build();

        // Add creator to the group
        group.addUser(creator);

        // Add other users
        for (Integer userId : userIds) {
            userRepository.findById(userId).ifPresent(group::addUser);
        }

        return groupRepository.save(group);
    }

    public Optional<Group> getGroupById(Integer groupId) {
        return groupRepository.findById(groupId);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public List<Group> getUserGroups(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return groupRepository.findAll().stream()
                .filter(group -> group.isMember(user))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addMemberToGroup(Integer groupId, Integer userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.addUser(user);
        groupRepository.save(group);
    }

    @Transactional
    public void removeMemberFromGroup(Integer groupId, Integer userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has unsettled balances
        // This is a simplified check - in production, you'd want to verify all settlements

        group.removeUser(user);
        groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Integer groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // In production, check if there are unsettled expenses before deleting
        groupRepository.delete(group);
    }
}
