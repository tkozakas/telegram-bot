package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.DailyMessageService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public abstract class Handler implements CommandHandler {
    protected final BotProperties botProperties;
    protected final DailyMessageService dailyMessageService;
    protected final MessageBuilderFactory messageBuilderFactory;

    protected Handler(DailyMessageService dailyMessageService, BotProperties botProperties, MessageBuilderFactory messageBuilderFactory) {
        this.dailyMessageService = dailyMessageService;
        this.botProperties = botProperties;
        this.messageBuilderFactory = messageBuilderFactory;
    }

    protected List<Validable> getReplyMessage(Long chatId, Integer messageId, String s) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(s)
                .enableMarkdown(false)
                .build());
    }

    protected List<Validable> getMessage(Long chatId, String s) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(s)
                .build());
    }

    protected List<Validable> getAnimationMessage(Long chatId, File file, String caption) {
        return List.of(messageBuilderFactory
                .createAnimationMessageBuilder(chatId)
                .withAnimation(file)
                .withCaption(caption)
                .build());
    }

    protected List<Validable> getPhotoMessage(Long chatId, File file,  String caption) {
        return List.of(messageBuilderFactory
                .createPhotoMessageBuilder(chatId)
                .withPhoto(file)
                .withCaption(caption)
                .build());
    }

    protected List<Validable> getStickerMessage(Long chatId, Sticker randomSticker) {
        return List.of(messageBuilderFactory
                .createStickerMessageBuilder(chatId)
                .withSticker(randomSticker.getFileId())
                .build());
    }

    protected List<Validable> getReplySticker(Long chatId, Sticker randomSticker, Integer messageId) {
        return List.of(messageBuilderFactory
                .createStickerMessageBuilder(chatId)
                .withSticker(randomSticker.getFileId())
                .withReplyToMessageId(messageId)
                .build());
    }

    protected List<Validable> getVideoMessage(Long chatId, File existingFile) {
        return List.of(messageBuilderFactory
                .createVideoMessage(chatId)
                .withVideo(existingFile)
                .build());
    }
}
