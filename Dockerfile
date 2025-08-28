# Stage 1: Build the JAR using Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Set work directory
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the project (skip tests if needed)
RUN mvn clean package -DskipTests

# Stage 2: Create a minimal runtime image
FROM eclipse-temurin:21-jdk

# Set work directory
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose ports if needed (not strictly required for SSH monitoring)
# EXPOSE 8080

# Environment variables (defaults)
ENV LOG_LEVEL=INFO \
    AUTH_LOG_PATH=/var/log/auth.log \
    TELEGRAM_TOKEN= \
    TELEGRAM_CHAT_ID=

# Run the JAR
ENTRYPOINT ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "app.jar"]
