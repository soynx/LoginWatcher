# Stage 1: Build the fat JAR
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy fat JAR built in previous stage
COPY --from=build /app/target/app.jar app.jar

# No need to copy logback.xml if it's in src/main/resources

ENV LOG_LEVEL=INFO \
    AUTH_LOG_PATH=/var/log/auth.log \
    TELEGRAM_TOKEN= \
    TELEGRAM_CHAT_ID=

ENTRYPOINT ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "app.jar"]
