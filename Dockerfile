FROM maven:3-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

RUN java -Djarmode=layertools -jar target/telegram-bot*.jar extract

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get -y update && apt-get -y upgrade \
  && apt-get install -y --no-install-recommends ffmpeg \
  && apt-get clean

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

RUN addgroup telegram-bot \
  && adduser --ingroup telegram-bot --disabled-password telegram-bot \
  && chown -R telegram-bot:telegram-bot /app

USER telegram-bot

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

# docker build -t tomas6446/telegram-api-bot:latest .
