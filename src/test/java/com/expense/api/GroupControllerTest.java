package com.expense.api;

import com.expense.api.controller.GroupController;
import com.expense.api.entity.Group;
import com.expense.api.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateGroup() {
        List<Integer> userIds = Arrays.asList(1, 2, 3);
        Group group = new Group();
        group.setId(1);
        group.setName("Friends");

        when(groupService.createGroup("Friends", userIds)).thenReturn(group);

        Group createdGroup = groupController.createGroup("Friends", userIds);
        assertNotNull(createdGroup);
        assertEquals(1, createdGroup.getId());
        assertEquals("Friends", createdGroup.getName());
        verify(groupService, times(1)).createGroup("Friends", userIds);
    }

    @Test
    public void testGetAllGroups() {
        Group group1 = new Group();
        group1.setId(1);
        group1.setName("Group1");
        Group group2 = new Group();
        group2.setId(2);
        group2.setName("Group2");

        when(groupService.getAllGroups()).thenReturn(Arrays.asList(group1, group2));

        List<Group> groups = groupController.getAllGroups();
        assertEquals(2, groups.size());
        verify(groupService, times(1)).getAllGroups();
    }

    @Test
    public void testGetGroupByIdExists() {
        Group group = new Group();
        group.setId(1);
        group.setName("Friends");

        when(groupService.getGroupById(1)).thenReturn(Optional.of(group));
        Group result = groupController.getGroup(1);

        assertNotNull(result);
        assertEquals("Friends", result.getName());
        verify(groupService, times(1)).getGroupById(1);
    }
}
