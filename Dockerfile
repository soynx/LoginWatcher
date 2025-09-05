FROM maven:3.9.5-eclipse-temurin-21

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# ENTRYPOINT ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "app.jar"]
ENTRYPOINT ["java", "-jar", "/app/target/app.jar"]
