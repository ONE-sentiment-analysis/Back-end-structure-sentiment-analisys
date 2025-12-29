FROM openjdk:17-jdk-slim
LABEL authors="pablo"

WORKDIR /app

COPY target/sentiment_analysis-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
