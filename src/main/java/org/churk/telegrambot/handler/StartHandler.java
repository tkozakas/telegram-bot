package org.churk.telegrambot.handler;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.DailyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
public class StartHandler extends Handler {
    public StartHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.START;
    }
}
