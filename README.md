# Telegram Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.

## Configuration
- Create a bot using [BotFather](https://t.me/botfather) and get the token.
- Create a group and add the bot to the group.

## Deployment

### Development
#### Start Postgres
```bash
  cd docker && docker-compose -f dev-compose.example.yaml up -d && cd ..
```
#### Start the application using Maven
```bash
  mvn clean install
  mvn spring-boot:run
```
#### Start the application using IntelliJ
- Create a new run configuration with the following parameters:
  - Main class: `com.tomaszoboza.telegramapi.TelegramApiApplication`
  - Environment variables: `BOT_USERNAME=<your_bot_username>`
  - Environment variables: `BOT_TOKEN=<your_bot_token>`
  - Environment variables: `BOT_WINNER_NAME=<your_bot_winner_name>`

## Production
```bash
  docker build -t tomas6446/telegram-api-bot .
  cd docker && docker-compose -f prod-compose.example.yaml up -d && cd .. 
```

## Features

### User Interaction and Command Handling
- Handles various commands:
  - `/commandreg`: Register yourself a game.
  - `/command`: Get today's winner.
  - `/commandstats`: Get stats (use /pidorstats [year] for specific year).
  - `/commandall`: Get all-time stats.
  - `/commandme`: Get personal stats.
  - `/reddit`: Get a random picture from subreddits.
  - `/fact`: Get a random fact.

### Features
- Sends scheduled message for daily updates of who is the winner of the day and memes.
- Multiple Messages Typing Animation
  
## Implementation Details
- Developed using Java and Spring Boot framework.
- Integrates with Telegram Bot API for messaging and user interaction.
- Uses Postgres database for storing data.
- Uses Docker for deployment.
- Uses Maven for dependency management.
- Uses Meme API for memes.
- Uses OpenJDK 21.
