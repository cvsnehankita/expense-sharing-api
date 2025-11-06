package com.expense.api.service;

import com.expense.api.entity.Group;
import com.expense.api.repository.GroupRepository;
import com.expense.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public Group createGroup(String groupName, List<Integer> userIds) {
        Group group = new Group();
        group.setName(groupName);
        for (Integer id : userIds) {
            userRepository.findById(id).ifPresent(group::addUser);
        }
        return groupRepository.save(group);
    }

    public Optional<Group> getGroupById(Integer groupId) {
        return groupRepository.findById(groupId);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
}
