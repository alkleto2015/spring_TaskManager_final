package com.example.spring_taskmanager_final.controller_Test;

import com.example.spring_taskmanager_final.controller.AuthController;
import com.example.spring_taskmanager_final.security_token.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.spring_taskmanager_final.controller.AuthController;
import com.example.spring_taskmanager_final.model.User;
import com.example.spring_taskmanager_final.repository.UserRepository;
import com.example.spring_taskmanager_final.security_token.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем безопасность, чтобы эндпоинт был доступен без токена
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil; // Мокаем утилиту генерации токена

    @MockitoBean
    private UserRepository userRepository; // Мокаем репозиторий пользователей

    @Test
    void shouldReturnTokenOnSuccessfulLogin() throws Exception {
        // Arrange: создаем фейкового пользователя в базе
    User mockUser = new User();
    mockUser.setUsername("admin");
    mockUser.setPassword("userPassword");
    mockUser.setRole("ADMIN");

        // Настраиваем моки:
        when(userRepository.findByUsername("admin")).thenReturn(mockUser);

        // Arrange: настраиваем мок, чтобы при запросе токена для "admin" возвращалась заглушка
        // Учитываем, что generateToken теперь принимает два аргумента (username и роль)
        when(jwtTokenUtil.generateToken(eq("admin"), eq("ADMIN"))).thenReturn("fake-jwt-token-123");

        // Act & Assert: отправляем POST-запрос с JSON в теле и проверяем ответ
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"userPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token-123"));
    }
}
