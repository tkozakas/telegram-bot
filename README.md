# Telegram Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.
This bot supports a variety of commands to interact with different functionalities. 
Uses this api [Meme-api](https://github.com/tkozakas/meme-api) to get memes, news, and so on. 
Below is a list of available commands and descriptions:

- `/start` - Start the bot.
- `/help` - Display the list of available commands.
- `/news` - Get the latest news.
- `/fact` - Get a random fact.
- `/stickers` - Get a random sticker.
- `/reddit` or `/meme` - Get a random post from a subreddit.
- `/shitpost` - Get a random shitpost.
- `/tts` - Convert text to speech.
- `/gpt` - Generate text using GPT-3.
- `/logs` - Get the logs of the bot.

## Configuration

- Obtain bot token and bot username through [BotFather](https://t.me/botfather).
- Add your bot to a Telegram group.

# Running the Bot
In order to run the bot, you need to create a `.env` file in the root directory of the project with the following environment variables:
### Required
```env
BOT_TOKEN=<<YOUR_BOT_TOKEN>>
BOT_USERNAME=<<YOUR_BOT_USERNAME>>
BOT_WINNER_NAME=<<YOUR_BOT_WINNER_NAME>>
```
### Optional
Follow the guide [here](https://github.com.tkozakas/meme-api) to get other features working.

### Docker Setup
Run the following command to start the bot in a Docker container:
```bash
docker-compose -f docker/dev-compose.yaml up -d
```

### Maven Setup
Run the following commands to start the bot using Maven:
```bash
mvn clean install
mvn spring-boot:run
```

# Changing the Bot's Behavior
You can change the bot's responses by modifying csv files in the `src/main/resources/changelog/data` directory.
