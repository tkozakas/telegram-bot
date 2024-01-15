# Telegram Pidor Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.

## Deployment
```bash
  # Clone repository
  git clone https://github.com/tomas6446/telegram-pibot
  # Enter the directory
  cd telegram-pibot

  # Run Postgres container
  docker compose up -d

  # Copy and run the script inside container
  docker cp init-db.sql telegram-pibot-postgres:/init-db.sql
  docker exec -u postgres telegram-pibot-postgres psql -d telegram_pibot -f /init-db.sql

  # Build and run project
  mvn clean install
  mvn spring-boot:run
```

## Features

### User Interaction and Command Handling
- Handles various commands:
  - `/pidoreg`: Register yourself as a pidor.
  - `/pidor`: Get today's pidor.
  - `/pidorstats`: Get stats (use /pidorstats [year] for specific year).
  - `/pidorall`: Get all-time stats.
  - `/pidorme`: Get personal stats.

### Scheduled Message Processing
- Sends scheduled message for daily updates of who is the pidor of the day.

### Statistics Management
- Manages 'Stats' objects representing the users' game statistics.
- Functions include registering users, updating scores, and selecting winners.

## Implementation Details
- Developed using Java and Spring Boot framework.
- Integrates with Telegram Bot API for messaging and user interaction.
