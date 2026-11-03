package com.example.spring_taskmanager_final.line_runner_test;

import com.example.spring_taskmanager_final.model.Task;
import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.TaskRepository;
import com.example.spring_taskmanager_final.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDatabaseRunner implements CommandLineRunner { // 2. Добавлен implements CommandLineRunner{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        testDatabase();
    }

    public void testDatabase() {
        System.out.println("=== СТАРТ ТЕСТУВАННЯ БД ===");

        User user = userRepository.findByUsername("admin");
        if (user != null) {
            System.out.println("Користувач: " + user.getUsername());

            List<Task> tasks = taskRepository.findByUserId(user.getId());
            tasks.forEach(task -> System.out.println("Завдання: " + task.getTitle()));
        } else {
            System.out.println("Користувача 'admin' не знайдено!");
        }
        System.out.println("=== КІНЕЦЬ ТЕСТУВАННЯ БД ===");
    }
}
