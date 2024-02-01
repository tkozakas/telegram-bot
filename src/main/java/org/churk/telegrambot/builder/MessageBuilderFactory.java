package org.churk.telegrambot.builder;

import org.springframework.stereotype.Service;

@Service
public class MessageBuilderFactory {
    public <T> T createBuilder(Long chatId, Class<T> builderClass) {
        if (builderClass == TextMessageBuilder.class) {
            return (T) new TextMessageBuilder(chatId);
        } else if (builderClass == PhotoMessageBuilder.class) {
            return (T) new PhotoMessageBuilder(chatId);
        } else if (builderClass == AnimationMessageBuilder.class) {
            return (T) new AnimationMessageBuilder(chatId);
        } else if (builderClass == StickerMessageBuilder.class) {
            return (T) new StickerMessageBuilder(chatId);
        } else if (builderClass == VideoMessageBuilder.class) {
            return (T) new VideoMessageBuilder(chatId);
        }
        throw new IllegalArgumentException("Unsupported builder class: " + builderClass.getName());
    }
}
