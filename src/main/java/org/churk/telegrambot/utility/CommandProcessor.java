package org.churk.telegrambot.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.chat.Chat;
import org.churk.telegrambot.chat.ChatService;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.*;
import org.churk.telegrambot.message.DailyMessageService;
import org.churk.telegrambot.stats.StatsService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

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

    public List<Validable> handleCommand(Update update) {
        if (update != null && !chatService.isChatExists(update.getMessage().getChatId())) {
            return handleBotAddedToGroup(update);
        }
        Message message = update.getMessage();
        String firstName = message.getFrom().getFirstName();
        String messageText = message.getText();
        log.info("{}: {}", firstName, messageText);

        List<String> arguments = List.of(messageText.split(" ")).subList(1, messageText.split(" ").length);
        Command command = Command.getTextCommand(messageText, botProperties.getWinnerName());
        CommandHandler handler = handlerFactory.getHandler(command);
        return handler.handle(HandlerContext.builder()
                .update(update)
                .args(arguments)
                .build());
    }

    private List<Validable> handleBotAddedToGroup(Update update) {
        String groupName = update.getMessage().getChat().getTitle();
        String firstName = update.getMessage().getFrom().getFirstName();
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();

        log.info("Bot added to group: {} (ID: {})", groupName, chatId);
        chatService.addChat(update);
        return messageBuilderFactory.getBuilder(MessageType.TEXT).build(Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.REPLY_TO_MESSAGE_ID, messageId,
                MessageParams.TEXT, dailyMessageService.getKeyNameSentence("welcome_message").formatted(firstName)
        ));
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
