package org.churk.telegrambot.builder;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class PhotoMessageBuilder {
    private final SendPhoto message;

    public PhotoMessageBuilder(Long chatId) {
        this.message = new SendPhoto();
        this.message.setChatId(String.valueOf(chatId));
    }

    public PhotoMessageBuilder withPhoto(File file) {
        message.setPhoto(new InputFile(file));
        return this;
    }

    public PhotoMessageBuilder withCaption(String caption) {
        message.setCaption(caption);
        return this;
    }

    public PhotoMessageBuilder withReplyToMessageId(Integer messageId) {
        message.setReplyToMessageId(messageId);
        return this;
    }

    public SendPhoto build() {
        return message;
    }
}
