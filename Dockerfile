FROM maven:3-eclipse-temurin-21 as builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=builder /app/target/telegram-bot*.jar /app.jar

RUN apt-get -y update && apt-get -y upgrade && apt-get install -y --no-install-recommends ffmpeg

ENTRYPOINT ["java", "-jar", "/app.jar"]

# docker build -t tomas6446/telegram-api-bot .