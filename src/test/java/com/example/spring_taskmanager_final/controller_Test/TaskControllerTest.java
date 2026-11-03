package com.example.spring_taskmanager_final.controller_Test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Запускаємо з веб-сервером
@AutoConfigureMockMvc(addFilters = false) // Отключаем Spring Security на время теста, Налаштовуємо MockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnTaskWhenExist() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Complete homework"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound());
    }
}
