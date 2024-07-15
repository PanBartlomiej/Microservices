# Etap 1: Budowanie aplikacji
FROM gradle:jdk19 AS builder

WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build

# Etap 2: Przygotowanie obrazu aplikacji
FROM openjdk:22-ea-19

WORKDIR /app

COPY --from=builder /app/build/libs/Config-server-*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
