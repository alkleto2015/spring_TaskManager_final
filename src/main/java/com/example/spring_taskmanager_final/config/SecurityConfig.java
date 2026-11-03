package com.example.spring_taskmanager_final.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.spring_taskmanager_final.security_token.JwtAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Включает проверку ролей через аннотации (@PreAuthorize)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    // Внедряем наш JWT-фильтр
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1 Отключаем CSRF (для stateless REST API)
                .csrf(AbstractHttpConfigurer::disable)

                //2 Отключаем сессии Stateless (JWT работает без сессий)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3 Настраиваем правила доступа к эндпоинтам
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем всем доступ к логину/регистрации и H2 консоли - публичные эндпоинты
                        .requestMatchers("/api/v1/auth/**", "/h2-console/**").permitAll()
                        // Регистрация нового пользователя (POST /api/v1/users) — открыта для всех
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        //Открытые системные эндпоинты для мониторинга (Health и Prometheus)
                        .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                        //Требуем авторизацию для работы с пользователями и задачами(защищаем эти эндпоинты)
                        .requestMatchers("/api/v1/users/**", "/api/v1/tasks/**").authenticated()
                        //работа с остальными метриками и логами Actuator — только для ADMIN
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // Все остальные запросы по умолчанию требуют авторизацию (безопасный подход)
                        .anyRequest().authenticated()
                )

                // 4. Добавляем наш JWT-фильтр перед стандартным фильтром проверки логина/пароля
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Убрали .httpBasic(Customizer.withDefaults()), так как теперь используем JWT
        // Разрешаем фреймы для корректной работы H2-консоли в браузере
        http.headers(headers ->
                        headers.frameOptions(frame -> frame.disable()))

        //для перехвата и обработки ошибки 403- Доступ заборонено: немає прав для перегляду цього ресурсу
        .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response,
                                      accessDeniedException) -> {
                    throw accessDeniedException; // Пробрасываем дальше в GlobalExceptionHandler
                })
        );

        return http.build();
    }

    // Бин для безопасного хэширования паролей (BCrypt + соль)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
    }
}

/*
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Включает проверку ролей через аннотации (@PreAuthorize)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Отключаем CSRF (для stateless REST API)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Отключаем сессии (JWT работает без сессий)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Настраиваем правила доступа к эндпоинтам
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты (логин, H2 консоль, health-чек)
                        .requestMatchers("/api/v1/auth/**", "/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // Регистрация нового пользователя (POST /api/v1/users) — открыта для всех
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                        // Защищенные эндпоинты (задачи, остальные операции с пользователями и метрики)
                        .requestMatchers("/api/v1/users/**", "/api/v1/tasks/**", "/actuator/**").authenticated()

                        // Всё остальное требует авторизацию
                        .anyRequest().authenticated()
                )

                // 4. Добавляем наш JWT-фильтр перед стандартным фильтром аутентификации
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Разрешаем отображение фреймов для корректной работы H2-консоли в браузере
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // Бин для безопасного хэширования паролей (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

*/

/*
ЭТО ПРАВИЛЬНЫЙ КЛАСС - ДО ТОГО КАК Я НАПСАЛ ТОКЕН. ТЕПЕРЬ ЕГО НАДО ИСПРАВЛЯТЬ - ДОБАВЛЯТЬ И ЧТО-ТО СТАРОЕ ВЫБРАСЫВАТЬ

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //відключаємо csrf- для  REST API через метод-ссилку або лямбду
                .csrf(AbstractHttpConfigurer::disable)

                //настроїмо правила доступу до ендпоінтів
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**").authenticated() //потребують авторизації для /users/**
                        .anyRequest().permitAll()) // всі інші запроси доступні для всіх

        //вмикаємо HTTP Basic з настройками за замовчуванням
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
*/
