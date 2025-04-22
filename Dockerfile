FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -f pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/target/videoframe-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-jar", "app.jar"]
