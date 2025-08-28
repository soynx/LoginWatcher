# Stage 1: Build the fat JAR with Maven inside the container
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Build the project and produce a shaded JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the JAR built in the previous stage
COPY --from=build /app/target/app.jar app.jar

# Copy logback configuration
COPY logback.xml logback.xml

# Environment variables
ENV LOG_LEVEL=INFO \
    AUTH_LOG_PATH=/var/log/auth.log \
    TELEGRAM_TOKEN= \
    TELEGRAM_CHAT_ID=

# Run the JAR
ENTRYPOINT ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "app.jar"]
