package com.example.spring_taskmanager_final.service_Test;

import com.example.spring_taskmanager_final.exception.ResourceNotFoundException;
import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.UserRepository;
import com.example.spring_taskmanager_final.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Підключаємо інтеграцію з Mockito
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserWhenFound() {
        // Arrange
        Long userId = 1l;
        User mockUser = new User(userId, "John Doe", "john.doe@example.com", null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));// "Мокаємо" поведінку репозиторія

        //Act
        User result = userService.getUserById(userId);

        //Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getUsername());
        verify(userRepository, times(1)).findById(userId);// Перевіряємо, що метод був викликаний рівно один раз
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }
}
