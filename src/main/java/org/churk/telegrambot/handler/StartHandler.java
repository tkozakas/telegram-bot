package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.service.ChatService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class StartHandler implements CommandHandler {
    private ChatService chatService;

    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String chatName = update.getMessage().getChat().getTitle();
        chatService.saveChat(chatId, chatName);
        return List.of();
    }

    @Override
    public List<Validable> handleByChatId(Long chatId) {
        return List.of();
    }
}
