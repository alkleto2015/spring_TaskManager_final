package com.example.spring_taskmanager_final.service_Test;

import com.example.spring_taskmanager_final.exception.ResourceNotFoundException;
import com.example.spring_taskmanager_final.model.Task;
import com.example.spring_taskmanager_final.repository.TaskRepository;
import com.example.spring_taskmanager_final.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Підключаємо інтеграцію з Mockito
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldReturnTaskWhenFound() {
        //Arrange
        Long taskId = 1L;
        Task mockTask = new Task(taskId, "Complete home work", "Finish math and science homework", null, null, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));//"Мокаємо" поведінку репозиторія

        //Act
        Task result = taskService.getTaskById(taskId);

        //Assert
        assertNotNull(result);
        assertEquals("Complete home work", result.getTitle());
        verify(taskRepository, times(1)).findById(taskId); // Перевіряємо, що метод був викликаний рівно один раз
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        //Arrange
        Long taskId = 999L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(taskId));
    }
}
