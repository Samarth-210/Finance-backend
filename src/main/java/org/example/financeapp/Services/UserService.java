package org.example.financeapp.Services;

import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.ExpenseRepository;
import org.example.financeapp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
       return userRepository.findByUserName(username).orElse(null);
    }
    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }
    public List<User>  findAll() {
        return userRepository.findAll();
    }
}
