# Telegram Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.

## Configuration
- Create a bot using [BotFather](https://t.me/botfather) and get the token.
- Create a group and add the bot to the group.

### Optional
- Add facts to the database using the .csv file in the resources folder.
- Add daily scheduled messages to the database using the .csv file in the resources folder.
- Change the winner message in the application.yaml in the resources folder (changes the commands).

## Deployment

### Development
```bash
  cd docker && docker-compose --profile dev up -d && cd ..
  
  # Run the script
  docker cp init-db.sql telegram-bot-postgres:/init-db.sql
  docker exec -u postgres telegram-bot-postgres psql -d telegram_bot -f /init-db.sql
  
  # Build and run project
  mvn clean install
  mvn spring-boot:run
```
#### Updating the database
If the PostgreSQL container has been initialized before and you want to re-run the init-db.sql script, 
you'll need to reset the database by removing the container and its volume (docker-compose down -v), 
but be cautious as this will erase your existing database.
```bash
  docker-compose down -v
  docker-compose --profile dev up -d
```

## Production
```bash
  cd docker && docker-compose --profile prod up -d && cd .. 
```

## Features

### User Interaction and Command Handling
- Handles various commands:
  - `/commandreg`: Register yourself a game.
  - `/command`: Get today's winner.
  - `/commandstats`: Get stats (use /pidorstats [year] for specific year).
  - `/commandall`: Get all-time stats.
  - `/commandme`: Get personal stats.
  - `/meme`: Get a random meme.
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
