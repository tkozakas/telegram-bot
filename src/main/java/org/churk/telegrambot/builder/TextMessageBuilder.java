package org.churk.telegrambot.builder;

import lombok.NoArgsConstructor;
import org.churk.telegrambot.handler.MessageParams;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;


@NoArgsConstructor
public class TextMessageBuilder implements MessageBuilder {
    private final SendMessage message = new SendMessage();

    public List<Validable> build(Map<MessageParams, Object> params) {
        params.forEach((key, value) -> {
            switch (key) {
                case MessageParams.CHAT_ID -> message.setChatId(String.valueOf(value));
                case MessageParams.TEXT -> message.setText((String) value);
                case MessageParams.REPLY_TO_MESSAGE_ID -> message.setReplyToMessageId((Integer) value);
                case MessageParams.MARKDOWN -> message.setParseMode("Markdown");
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return List.of(message);
    }
}
