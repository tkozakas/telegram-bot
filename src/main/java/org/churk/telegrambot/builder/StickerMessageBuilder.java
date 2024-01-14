package org.churk.telegrambot.builder;

import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public class StickerMessageBuilder {
    private final SendSticker message;

    public StickerMessageBuilder(Long chatId) {
        this.message = new SendSticker();
        this.message.setChatId(String.valueOf(chatId));
    }

    public StickerMessageBuilder withSticker(String sticker) {
        message.setSticker(new InputFile(sticker));
        return this;
    }

    public SendSticker build() {
        return message;
    }
}
