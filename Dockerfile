FROM maven:3.9.8-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY . app/

RUN mvn -f app/pom.xml clean package -Dmaven.test.skip=true

ENTRYPOINT ["java", "-jar", "app/target/videoframe-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080/tcp
