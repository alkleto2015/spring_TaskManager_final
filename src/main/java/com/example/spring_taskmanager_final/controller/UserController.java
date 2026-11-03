package com.example.spring_taskmanager_final.controller;

import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Доступно ТОЛЬКО администратору
    public List<User> getAllUsers() {
        // Повертає список усіх користувачів
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // Отримує користувача за id
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        // Створює нового користувача
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        // Оновлює дані користувача
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // Видаляє користувача
        userService.deleteUser(id);
    }
}
