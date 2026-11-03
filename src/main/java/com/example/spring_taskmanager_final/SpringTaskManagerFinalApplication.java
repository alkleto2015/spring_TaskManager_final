package com.example.spring_taskmanager_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class}) // <-- Добавили исключение
public class SpringTaskManagerFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTaskManagerFinalApplication.class, args);
    }

}
