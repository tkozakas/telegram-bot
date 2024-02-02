package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.churk.telegrambot.builder.*;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.message.DailyMessageService;
import org.churk.telegrambot.sticker.Sticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public abstract class Handler implements CommandHandler {
    @Autowired
    protected BotProperties botProperties;
    @Autowired
    protected DailyMessageService dailyMessageService;
    @Autowired
    protected MessageBuilderFactory messageBuilderFactory;

    protected <T> List<Validable> createMessage(Long chatId, MessageBuilderFunction<T> builderFunction, Class<T> builderClass) {
        T builder = messageBuilderFactory.createBuilder(chatId, builderClass);
        return List.of(builderFunction.build(builder));
    }


    protected List<Validable> getReplyMessage(Long chatId, Integer messageId, String s) {
        return createMessage(chatId, builder -> builder
                .withReplyToMessageId(messageId)
                .withText(s)
                .build(), TextMessageBuilder.class);
    }

    protected List<Validable> getMessage(Long chatId, String s) {
        return createMessage(chatId, builder -> builder
                .withText(s)
                .build(), TextMessageBuilder.class);
    }

    protected List<Validable> getAnimationMessage(Long chatId, File file, String caption) {
        return createMessage(chatId, builder -> builder
                .withAnimation(file)
                .withCaption(caption)
                .build(), AnimationMessageBuilder.class);
    }

    protected List<Validable> getPhotoMessage(Long chatId, File file, String caption) {
        return createMessage(chatId, builder -> builder
                .withPhoto(file)
                .withCaption(caption)
                .build(), PhotoMessageBuilder.class);
    }

    protected List<Validable> getStickerMessage(Long chatId, Sticker randomSticker) {
        return createMessage(chatId, builder -> builder
                .withSticker(randomSticker.getFileId())
                .build(), StickerMessageBuilder.class);
    }

    protected List<Validable> getReplySticker(Long chatId, Sticker randomSticker, Integer messageId) {
        return createMessage(chatId, builder -> builder
                .withSticker(randomSticker.getFileId())
                .withReplyToMessageId(messageId)
                .build(), StickerMessageBuilder.class);
    }

    protected List<Validable> getVideoMessage(Long chatId, File existingFile) {
        return createMessage(chatId, builder -> builder
                .withVideo(existingFile)
                .build(), VideoMessageBuilder.class);
    }
}
