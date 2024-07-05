# Etap 1: Budowanie aplikacji
FROM gradle:jdk19 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew build

# Etap 2: Przygotowanie obrazu aplikacji
FROM openjdk:22-ea-19

WORKDIR /app

COPY --from=builder /app/build/libs/Ewidencja-*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
