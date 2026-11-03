package com.example.spring_taskmanager_final.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricService {

    private final Counter userCreatedCounter;

    public CustomMetricService(MeterRegistry meterRegistry) {
        // Создаем счетчик (Counter), который будет собираться Prometheus
        this.userCreatedCounter = Counter.builder("app.users.created.total")
                .description("Загальна кількість створених користувачів")
                .tag("service", "task-manager")
                .register(meterRegistry);
    }

    // Метод для увеличения счетчика на 1
    public void incrementUserCreated() {
        userCreatedCounter.increment();
    }
}
