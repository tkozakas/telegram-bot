# Stage 1: Build the application
FROM maven:3.8.7-openjdk-18 AS builder
WORKDIR /app
COPY src src
COPY pom.xml .
RUN mvn -f pom.xml clean package

# Stage 2: Create the runtime image
FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

# to build the image see the following command
# docker build -f docker/Dockerfile -t telegram-api-bot .
