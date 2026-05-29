# ── Этап 1: сборка ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# Копируем Gradle wrapper и конфигурацию отдельно — чтобы зависимости кешировались
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
COPY gradle/ gradle/

# Скачиваем зависимости (отдельный слой — пересборка не сбрасывает кеш)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q || true

# Копируем исходники и собираем JAR
COPY src/ src/
RUN ./gradlew build -x test --no-daemon

# ── Этап 2: runtime ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
