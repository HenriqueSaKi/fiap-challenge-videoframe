FROM maven:3.9.8-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY . .

RUN mvn package spring-boot:repackage -Dmaven.test.skip=true

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/videoframe-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
