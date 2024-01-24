package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class HelpHandler implements CommandHandler {
    private BotProperties botProperties;
    private MessageBuilderFactory messageBuilderFactory;

    @Override
    public List<Validable> handle(Update update) {
        return List.of();
    }

    @Override
    public List<Validable> handleByChatId(Long chatId) {
        return List.of();
    }
}
