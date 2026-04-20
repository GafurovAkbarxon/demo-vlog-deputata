FROM gradle:8.10.2-jdk17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew clean bootJar --no-daemon

# ---------- RUNTIME ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=builder /app/build/libs/*.jar app.jar

RUN mkdir -p /app/storage/uploads/images /app/storage/uploads/videos && \
    chown -R spring:spring /app

USER spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]