FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -f pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jdk

# 👇 Add necessary FFmpeg dependencies
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libavformat-dev \
    libavcodec-dev \
    libavutil-dev \
    libswscale-dev \
    libavfilter-dev \
    libavdevice-dev \
    libx264-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/videoframe-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080/tcp

ENTRYPOINT ["java", "-jar", "app.jar"]
