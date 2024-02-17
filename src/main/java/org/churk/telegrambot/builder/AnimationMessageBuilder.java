package org.churk.telegrambot.builder;

import lombok.NoArgsConstructor;
import org.churk.telegrambot.handler.game.message.MessageParams;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class AnimationMessageBuilder implements MessageBuilder {
    private final SendAnimation message = new SendAnimation();

    public List<Validable> build(Map<MessageParams, Object> params) {
        params.forEach((key, value) -> {
            switch (key) {
                case MessageParams.CHAT_ID -> message.setChatId(String.valueOf(value));
                case MessageParams.ANIMATION -> message.setAnimation(new InputFile((File) value));
                case MessageParams.CAPTION -> message.setCaption((String) value);
                case MessageParams.REPLY_TO_MESSAGE_ID -> message.setReplyToMessageId((Integer) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });
        return List.of(message);
    }
}
