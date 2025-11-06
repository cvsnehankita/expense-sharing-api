package com.expense.api.controller;

import com.expense.api.entity.User;
import com.expense.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    @PostMapping("/user")
    public User addUser(@RequestBody User user){
        return service.saveUser(user);
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Expense Sharing App!";
    }

    @GetMapping("/admin/dashboard")
    public String admin() {
        return "Admin Dashboard - only accessible by ADMIN";
    }

    @GetMapping("/user/dashboard")
    public String user() {
        return "User Dashboard - accessible by USER or ADMIN";
    }
}
