package org.churk.telegrambot.builder;

import org.springframework.stereotype.Service;

@Service
public class MessageBuilderFactory {
    public TextMessageBuilder createTextMessageBuilder(Long chatId) {
        return new TextMessageBuilder(chatId);
    }

    public PhotoMessageBuilder createPhotoMessageBuilder(Long chatId) {
        return new PhotoMessageBuilder(chatId);
    }

    public AnimationMessageBuilder createAnimationMessageBuilder(Long chatId) {
        return new AnimationMessageBuilder(chatId);
    }

    public StickerMessageBuilder createStickerMessageBuilder(Long chatId) {
        return new StickerMessageBuilder(chatId);
    }
}
