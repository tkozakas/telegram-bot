package org.churk.telegrambot.builder;

import lombok.NoArgsConstructor;
import org.churk.telegrambot.handler.game.message.MessageParams;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class VideoMessageBuilder implements MessageBuilder {
    private final SendVideo message = new SendVideo();

    public List<Validable> build(Map<MessageParams, Object> params) {
        params.forEach((key, value) -> {
            switch (key) {
                case MessageParams.CHAT_ID -> message.setChatId(String.valueOf(value));
                case MessageParams.VIDEO -> message.setVideo(new InputFile((String) value));
                case MessageParams.CAPTION -> message.setCaption((String) value);
                case MessageParams.REPLY_TO_MESSAGE_ID -> message.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return List.of(message);
    }
}
