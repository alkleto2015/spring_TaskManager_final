package com.example.spring_taskmanager_final.controller;

import com.example.spring_taskmanager_final.dto.AuthRequest;
import com.example.spring_taskmanager_final.dto.AuthResponse;
import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.UserRepository;
import com.example.spring_taskmanager_final.security_token.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;
import com.example.spring_taskmanager_final.dto.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // 1. Ищем пользователя в базе данных
        User user = userRepository.findByUsername(authRequest.getUsername());
        if(user == null) {
            return ResponseEntity.status(401).body(new ErrorResponse(401, "Невірний логін або пароль"));
        }

        // 2. Проверяем пароль напрямую через equals (без всякого BCrypt)
        if(!user.getPassword().equals(authRequest.getPassword())) {
            return ResponseEntity.status(401).body(new ErrorResponse(401, "Невірний логін або пароль"));
        }

        // 3. Генерируем токен и возвращаем
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}


