package com.example.spring_taskmanager_final.repository;

import com.example.spring_taskmanager_final.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
