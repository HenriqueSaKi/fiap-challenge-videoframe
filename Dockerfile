FROM maven:3.9.8-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .

RUN mvn -f pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/videoframe-0.0.1-SNAPSHOT.jar app.jar

RUN apt-get -y update
RUN apt-get -y upgrade
RUN apt-get install -y ffmpeg

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-Xmx6g", "-jar", "app.jar"]