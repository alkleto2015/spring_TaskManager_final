# Етап 1 - Збірка застосунку за допомогою Maven ---
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Копіюємо конфігурацію та вихідний код
COPY pom.xml .
COPY src ./src

# Збираємо проєкт (пропускаємо тести всередині докера)
RUN mvn clean package -DskipTests

# Етап 2: Створення легкого та безпечного продакшн-образу
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Копіюємо готовий JAR-файл із попереднього етапу (використовуємо зірочку для надійності)
COPY --from=builder /build/target/*.jar app.jar

# Відкриваємо порт
EXPOSE 8080

# Команда для запуску застосунку
ENTRYPOINT ["java", "-jar", "app.jar"]