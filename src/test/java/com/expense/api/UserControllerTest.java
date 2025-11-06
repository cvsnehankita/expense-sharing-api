package com.expense.api;

import com.expense.api.controller.UserController;
import com.expense.api.entity.User;
import com.expense.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
        import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // initialize mocks
    }

    @Test
    public void testAddUser() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        user.setUserName("John Doe");
        user.setUserEmail("john@example.com");

        when(userService.saveUser(user)).thenReturn(user);
        User savedUser = userController.addUser(user);
        assertNotNull(savedUser);
        assertEquals(1, savedUser.getUserId());
        assertEquals("John Doe", savedUser.getUserName());
        assertEquals("john@example.com", savedUser.getUserEmail());

        verify(userService, times(1)).saveUser(user);
    }

    @Test
    public void testHome() {
        String result = userController.home();
        assertEquals("Welcome to Expense Sharing App!", result);
    }

    @Test
    public void testAdminDashboard() {
        String result = userController.admin();
        assertEquals("Admin Dashboard - only accessible by ADMIN", result);
    }

    @Test
    public void testUserDashboard() {
        String result = userController.user();
        assertEquals("User Dashboard - accessible by USER or ADMIN", result);
    }
}
