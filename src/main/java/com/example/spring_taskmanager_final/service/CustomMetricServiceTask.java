package com.example.spring_taskmanager_final.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CustomMetricServiceTask {

    private final Counter taskCreatedCounter;

    public CustomMetricServiceTask(MeterRegistry meterRegistry) {
        // Создаем счетчик (Counter), который будет собираться Prometheus
        this.taskCreatedCounter = Counter.builder("app.tasks.created.total")
                .description("Загальна кількість створених задач")
                .tag("service", "task-manager")
                .register(meterRegistry);
    }

    // Метод для увеличения счетчика на 1
    public void incrementTaskCreated() {
        taskCreatedCounter.increment();
    }
}
