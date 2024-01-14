package org.churk.telegrambot.builder;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

public class AnimationMessageBuilder {
    private final SendAnimation message;

    public AnimationMessageBuilder(Long chatId) {
        this.message = new SendAnimation();
        this.message.setChatId(String.valueOf(chatId));
    }

    public AnimationMessageBuilder withAnimation(File file) {
        message.setAnimation(new InputFile(file));
        return this;
    }

    public AnimationMessageBuilder withCaption(String caption) {
        message.setCaption(caption);
        return this;
    }

    public AnimationMessageBuilder withReplyToMessageId(Integer messageId) {
        message.setReplyToMessageId(messageId);
        return this;
    }

    public SendAnimation build() {
        return message;
    }
}
