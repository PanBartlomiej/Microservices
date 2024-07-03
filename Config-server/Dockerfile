# Etap 1: Budowanie aplikacji
FROM maven:3.8.6-openjdk-19 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etap 2: Przygotowanie obrazu aplikacji
FROM openjdk:19-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/config-server-*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=classpath:/application.yml,file:/app/application.yml"]
