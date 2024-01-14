package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.factory.HandlerFactory;
import org.churk.telegrambot.model.Chat;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.ChatService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandProcessor {
    private final StatsService statsService;
    private final ChatService chatService;
    private final HandlerFactory handlerFactory;
    private final BotProperties botProperties;

    public List<Validable> handleCommand(Update update) {
        List<String> arguments = List.of(update.getMessage().getText().split(" "));
        Command command = Command.getTextCommand(arguments.getFirst(), botProperties.getWinnerName());
        if (command == null) {
            return List.of();
        }

        CommandHandler handler = handlerFactory.getHandler(command, arguments);
        return handler.handle(update);
    }

    public List<Validable> handleScheduledCommand(Command command) {
        CommandHandler handler = handlerFactory.getHandler(command, List.of());
        List<Chat> chats = chatService.getAllChats();
        return chats.stream()
                .flatMap(chat -> handler.handleByChatId(chat.getChatId())
                        .stream())
                .toList();
    }

    public void handleReset() {
        statsService.reset();
    }
}
