package com.example.spring_taskmanager_final.service;

import com.example.spring_taskmanager_final.exception.ResourceNotFoundException;
import com.example.spring_taskmanager_final.model.Task;
import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomMetricService customMetricService;  // Внедряем наши метрики

    @Autowired
    public UserService(UserRepository userRepository, CustomMetricService customMetricService) {
        this.userRepository = userRepository;
        this.customMetricService = customMetricService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));//здесь
        //заменили RuntimeException- на ResourceNotFoundException(чтобы выводилось сообщение об ошибке)
    }

    //немного изменил запись метода- добавил кастомный счетчик созданных пользователей
    public User createUser(User user) {
        // логика сохранения пользователя
        User savedUser = userRepository.save(user);

        // Увеличиваем наш кастомный счетчик при успешном создании!
        customMetricService.incrementUserCreated();

        return savedUser;
    }

    @Transactional
    public User updateUser(Long id, User userDetails) { // userDetails- это user,который пришел из контроллера с новыми данными
        // 1. Ищем существующего пользователя в БД
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з id " + id + " не знайдено"));
        //здесь тоже заменили RuntimeException- на ResourceNotFoundException

        //Переносим новые данные из обновленного объекта(userDetails) из контроллера -
        //в existingUser- обьект(сущность) из базы данных
        existingUser.setUsername(userDetails.getUsername()); //existingUser — сущность,которую мы только что достали из базы данных(наш user);
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPassword(userDetails.getPassword());
        existingUser.setRole(userDetails.getRole());

        // Обновляем пароль, если он передан
        if(userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
            existingUser.setPassword(userDetails.getPassword());
        }

        // 3. Обновляем список задач (если переданы новые задачи)
        if(userDetails.getTasks() != null) {
            // Очищаем старые привязки или обновляем коллекцию
            existingUser.getTasks().clear();
            for (Task task : userDetails.getTasks()) {
                task.setUser(existingUser); // Устанавливаем обратную связь (ОБЯЗАТЕЛЬНО для JPA!)
                existingUser.getTasks().add(task);
            }
        }

        // 4. Благодаря @Transactional изменения применятся автоматически,но явный save гарантирует немедленный ответ
        // Сохраняем обновленные данные
        return userRepository.save(existingUser);
    }


    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Користувача з id " + id + " не знайдено"));
        //здесь тоже заменили RuntimeException- на ResourceNotFoundException

        userRepository.delete(user);
    }
}
