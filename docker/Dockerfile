# Start with Amazon Corretto JDK 17
FROM amazoncorretto:17 AS build

# Install Maven
RUN yum update -y && \
    yum install -y maven

# Copy the project files into the container
COPY .. /usr/src/app

# Set the working directory
WORKDIR /usr/src/app

# Build the application
RUN mvn clean package

# Use Corretto 17 for the final image as well
FROM amazoncorretto:17

# Copy the JAR file from the build stage
COPY --from=build /usr/src/app/target/telegram-bot-0.0.1-SNAPSHOT.jar /usr/app/telegram-api-bot.jar

# Set the working directory
WORKDIR /usr/app

# Command to run the application
ENTRYPOINT ["java", "-jar", "telegram-api-bot.jar"]
