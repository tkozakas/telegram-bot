# Telegram Pidor Bot

Telegram bot implemented in Java, using Spring Boot and the Telegram Bot API.

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
