package com.expense.api.service;

import com.expense.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.expense.api.entity.User;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User saveUser(User user){
        return repository.save(user);
    }
    public Optional<User> findByUserName(String userName){
        return repository.findByUserName(userName);
    }
    public Optional<User> findByUserEmail(String userEmail){
        return repository.findByUserEmail(userEmail);
    }
}
