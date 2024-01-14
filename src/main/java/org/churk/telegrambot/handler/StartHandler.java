package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.service.ChatService;
import org.churk.telegrambot.service.DailyMessageService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class StartHandler implements CommandHandler {
    private MessageBuilderFactory messageBuilderFactory;
    private DailyMessageService dailyMessageService;
    private ChatService chatService;

    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String chatName = update.getMessage().getChat().getTitle();
        chatService.saveChat(chatId, chatName);

        String welcomeMessage = dailyMessageService.getKeyNameSentence("welcome_message");
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(update.getMessage().getChatId())
                .withText(welcomeMessage)
                .build());
    }

    @Override
    public List<Validable> handleByChatId(Long chatId) {
        return List.of();
    }
}
