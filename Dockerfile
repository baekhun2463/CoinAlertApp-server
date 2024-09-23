FROM openjdk:17-jdk-slim

LABEL authors="baekhun"

COPY build/libs/CoinAlertApp-server-0.0.1-SNAPSHOT.jar /app/CoinAlertApp.jar

ENTRYPOINT ["java", "-jar", "/app/CoinAlertApp.jar"]