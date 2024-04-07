package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilder;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.MessageParams;
import org.churk.telegrambot.model.MessageType;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.model.Sticker;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public abstract class Handler implements CommandHandler {
    @Autowired
    protected BotProperties botProperties;
    @Autowired
    protected DailyMessageService dailyMessageService;
    @Autowired
    protected MessageBuilderFactory messageBuilderFactory;

    protected List<Validable> createMessage(MessageType messageType, Map<MessageParams, Object> params) {
        MessageBuilder builder = messageBuilderFactory.getBuilder(messageType);
        return builder.build(params);
    }

    protected List<Validable> getReplyMessage(Long chatId, Integer messageId, String message) {
        return createMessage(MessageType.TEXT, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.TEXT, message,
                MessageParams.REPLY_TO_MESSAGE_ID, messageId
        ));
    }

    protected List<Validable> getReplyMessageWithMarkdown(Long chatId, Integer messageId, String message) {
        return createMessage(MessageType.TEXT, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.TEXT, message,
                MessageParams.REPLY_TO_MESSAGE_ID, messageId,
                MessageParams.MARKDOWN, true
        ));
    }

    protected List<Validable> getReplySticker(Long chatId, Integer messageId, Sticker sticker) {
        return createMessage(MessageType.STICKER, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.STICKER, sticker.getFileId(),
                MessageParams.REPLY_TO_MESSAGE_ID, messageId
        ));
    }

    protected List<Validable> getMessageWithMarkdown(Long chatId, String message) {
        return createMessage(MessageType.TEXT, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.TEXT, message,
                MessageParams.MARKDOWN, true
        ));
    }

    protected List<Validable> getMessage(Long chatId, String message) {
        return createMessage(MessageType.TEXT, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.TEXT, message
        ));
    }

    protected List<Validable> getSticker(Long chatId, Sticker sticker) {
        return createMessage(MessageType.STICKER, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.STICKER, sticker.getFileId()
        ));
    }

    protected List<Validable> getAnimation(Long chatId, File file, String caption) {
        return createMessage(MessageType.ANIMATION, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.ANIMATION, file,
                MessageParams.CAPTION, caption
        ));
    }

    protected List<Validable> getPhoto(Long chatId, File file, String caption) {
        return createMessage(MessageType.PHOTO, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.PHOTO, file,
                MessageParams.CAPTION, caption
        ));
    }

    protected List<Validable> getVideo(Long chatId, File file) {
        return createMessage(MessageType.VIDEO, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.VIDEO, file
        ));
    }
}
