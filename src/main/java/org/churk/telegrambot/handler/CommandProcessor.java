package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.factory.HandlerFactory;
import org.churk.telegrambot.model.bot.Chat;
import org.churk.telegrambot.model.bot.Command;
import org.churk.telegrambot.service.ChatService;
import org.churk.telegrambot.service.DailyMessageService;
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
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final RandomResponseHandler randomResponseHandler;

    public List<Validable> handleCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();
        String messageText = update.getMessage().getText();
        String firstName = update.getMessage().getFrom().getFirstName();
        log.info("{}: {}", firstName, messageText);

        List<String> arguments = List.of(messageText.split(" "));
        Command command = Command.getTextCommand(messageText, botProperties.getWinnerName());
        if (command == null) {
            return randomResponseHandler.handle(HandlerContext.builder()
                    .update(update)
                    .args(arguments)
                    .build());
        }
        if (command != Command.START && !chatService.isChatExists(update.getMessage().getChatId())) {
            return List.of(messageBuilderFactory.createTextMessageBuilder(chatId)
                    .withReplyToMessageId(messageId)
                    .withText(dailyMessageService.getKeyNameSentence("not_started"))
                    .build());
        }
        CommandHandler handler = handlerFactory.getHandler(command);
        return handler.handle(HandlerContext.builder()
                .update(update)
                .args(arguments)
                .build());
    }

    public List<Validable> handleScheduledCommand(Command command) {
        CommandHandler handler = handlerFactory.getHandler(command);
        List<Chat> chats = chatService.getAllChats();
        return chats.stream().flatMap(chat ->
                handler.handle(HandlerContext.builder()
                        .update(chat.getUpdate())
                        .args(List.of())
                        .build()).stream()).toList();
    }

    public void handleReset() {
        statsService.reset();
    }
}
