# Telegram Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.

## Configuration
- Obtain bot token through [BotFather](https://t.me/botfather).
- Add your bot to a Telegram group.
- Obtain a NewsAPI key from [NewsAPI](https://newsapi.org/).
All configuration is done through environment variables or application.yaml.

## Deployment
Deploy using Docker and Maven, or directly through IntelliJ with specific environment variables (`BOT_USERNAME`, `BOT_TOKEN`, `BOT_WINNER_NAME`).
### Docker Setup
```bash
# inside the project folder
docker-compose -f docker/dev-compose.yaml up -d
```
### Maven Setup
```bash
mvn clean install
mvn spring-boot:run
```
### IntelliJ Setup
- Add environment variables to run configuration:
  - `BOT_USERNAME`
  - `BOT_TOKEN`
  - `BOT_WINNER_NAME`
  - `NEWS_API_KEY` (optional)

## Commands: 
- User and game registration
- Daily winners
- Stats
- News and updates from NewsAPI
- Subreddit images
- Random facts.
