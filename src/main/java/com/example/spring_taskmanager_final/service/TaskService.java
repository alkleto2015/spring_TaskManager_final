package com.example.spring_taskmanager_final.service;

import com.example.spring_taskmanager_final.exception.ResourceNotFoundException;
import com.example.spring_taskmanager_final.model.Task;
import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.TaskRepository;
import com.example.spring_taskmanager_final.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CustomMetricServiceTask customMetricServiceTask;  // Внедряем наши метрики

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       CustomMetricServiceTask customMetricServiceTask) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.customMetricServiceTask = customMetricServiceTask;
    }

    public List<Task> getAllTask() {
        // Достаем имя текущего юзера прямо из контекста безопасности (из JWT-токена)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Ищем пользователя в базе
        User user = userRepository.findByUsername(username);

        // Если админ — отдаем всё
        if(user != null && "ADMIN".equals(user.getRole())) {
            return taskRepository.findAll();
        }
        // Если обычный юзер — только его задачи через готовый метод в ТаскРепозитории
        return taskRepository.findByUserId(user.getId());
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
       // здесь тоже заменили RuntimeException- на ResourceNotFoundException, чтобы выводилось
       // корректно в ДжейСоне сообщение об ошибке
    }

    //немного изменил запись метода- добавил кастомный счетчик задач
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        //  Увеличиваем наш кастомный счетчик при успешном создании!
        customMetricServiceTask.incrementTaskCreated();
        return savedTask;
    }

    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        // 1. Ищем существующую задачу в БД
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задачу з id " + id + " не знайдено"));
        //здесь тоже заменили RuntimeException- на ResourceNotFoundException

        // 2. Обновляем текстовые поля и дату
        //Переносим новые данные из обновленного объекта(taskDetails) из контроллера -
        //в existingTask- обьект(сущность) из базы данных
        existingTask.setTitle(taskDetails.getTitle()); //existingTask - сущность,которую мы только что достали из базы данных(наш Task)
        existingTask.setDescription(taskDetails.getDescription()); //taskDetails - обновленный объект(taskDetails) из контроллера
        existingTask.setDeadline(taskDetails.getDeadline());
        existingTask.setStatus(taskDetails.getStatus());

        // 3. Обновляем привязку к пользователю по user_id
        if (taskDetails.getUser() != null && taskDetails.getUser().getId() != null) {
            Long userId = taskDetails.getUser().getId();
            User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("Користувача з id " + userId + " не знайдено"));
            existingTask.setUser(user);
        }
        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Задачу з id " + id + " не знайдено"));
        //здесь тоже заменили RuntimeException- на ResourceNotFoundException

        taskRepository.delete(task);
    }
}
