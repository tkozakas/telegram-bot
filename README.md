# Telegram Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.

## Deployment
```bash
  # Clone repository
  git clone https://github.com/tomas6446/telegram-api-bot
  # Enter the directory
  cd telegram-api-bot

  # Run Postgres container
  docker compose up -d

  # Copy and run the script inside container
  docker cp init-db.sql telegram-bot-postgres:/init-db.sql
  docker exec -u postgres telegram-bot-postgres psql -d telegram_bot -f /init-db.sql

  # Build and run project
  mvn clean install
  mvn spring-boot:run
```

## Configuration
- Create a bot using [BotFather](https://t.me/botfather) and get the token.
- Create a group and add the bot to the group.

### Optional
- Add facts to the database using the .csv file in the resources folder.
- Add daily scheduled messages to the database using the .csv file in the resources folder.
- Change the winner message in the application.yaml in the resources folder (changes the commands).

## Features

### User Interaction and Command Handling
- Handles various commands:
  - `/pidoreg`: Register yourself a game.
  - `/pidor`: Get today's winner.
  - `/pidorstats`: Get stats (use /pidorstats [year] for specific year).
  - `/pidorall`: Get all-time stats.
  - `/pidorme`: Get personal stats.

### Features
- Sends scheduled message for daily updates of who is the winner of the day and memes.
- Multiple Messages Typing Animation
  
## Implementation Details
- Developed using Java and Spring Boot framework.
- Integrates with Telegram Bot API for messaging and user interaction.
