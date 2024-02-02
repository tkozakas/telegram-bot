package org.churk.telegrambot.builder;

import lombok.NoArgsConstructor;
import org.churk.telegrambot.handler.MessageParams;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class StickerMessageBuilder implements MessageBuilder {
    private final SendSticker message = new SendSticker();

    public List<Validable> build(Map<MessageParams, Object> params) {
        params.forEach((key, value) -> {
            switch (key) {
                case MessageParams.CHAT_ID -> message.setChatId(String.valueOf(value));
                case MessageParams.STICKER -> message.setSticker(new InputFile((File) value));
                case MessageParams.REPLY_TO_MESSAGE_ID -> message.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return List.of(message);
    }
}
