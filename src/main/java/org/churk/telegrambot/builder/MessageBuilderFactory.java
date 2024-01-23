package org.churk.telegrambot.builder;

import org.springframework.stereotype.Service;

@Service
public class MessageBuilderFactory {
    public TextMessageBuilder createTextMessageBuilder(Long chatId) {
        return new TextMessageBuilder(chatId);
    }
}
