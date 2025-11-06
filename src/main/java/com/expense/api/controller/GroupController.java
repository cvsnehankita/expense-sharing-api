package com.expense.api.controller;

import com.expense.api.entity.Group;
import com.expense.api.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public Group createGroup(@RequestParam String name, @RequestParam List<Integer> userIds) {
        return groupService.createGroup(name, userIds);
    }

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable Integer id) {
        return groupService.getGroupById(id).orElseThrow(() -> new RuntimeException("Group not exists"));
    }

}
