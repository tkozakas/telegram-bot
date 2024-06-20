FROM docker.io/maven:3-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

RUN java -Djarmode=layertools -jar target/telegram-bot*.jar extract

FROM docker.io/eclipse-temurin:21-jre
WORKDIR /app

RUN apt-get -y update \
  && apt-get install -y ffmpeg \
  && apt-get clean \
  && rm -rf /var/cache/apt/lists

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

# docker build -t tomas6446/telegram-api-bot:latest .
