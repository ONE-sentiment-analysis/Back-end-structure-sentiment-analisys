FROM eclipse-temurin:25-jdk-alpine

LABEL authors="pablo"

RUN mkdir -p /app

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
