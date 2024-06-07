package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.churk.telegrambot.builder.UnifiedMessageBuilder;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.MessageParams;
import org.churk.telegrambot.model.MessageType;
import org.churk.telegrambot.model.Sticker;
import org.churk.telegrambot.service.DailyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public abstract class Handler implements CommandHandler {
    @Autowired
    protected BotProperties botProperties;
    @Autowired
    protected DailyMessageService dailyMessageService;
    @Autowired
    protected UnifiedMessageBuilder unifiedMessageBuilder;

    protected List<Validable> createMessage(MessageType messageType, Map<MessageParams, Object> params) {
        return unifiedMessageBuilder.build(messageType, params);
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

    protected List<Validable> getVideo(Long chatId, File file, String caption) {
        return createMessage(MessageType.VIDEO, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.VIDEO, file,
                MessageParams.CAPTION, caption
        ));
    }

    protected List<Validable> getMediaGroup(Long chatId, List<InputMedia> files) {
        return createMessage(MessageType.MEDIA_GROUP, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.MEDIA_GROUP, files
        ));
    }

    protected List<Validable> getReplyAudioMessage(Long chatId, Integer messageId, String message, File audioMessage) {
        return createMessage(MessageType.AUDIO, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.AUDIO, audioMessage,
                MessageParams.CAPTION, message,
                MessageParams.REPLY_TO_MESSAGE_ID, messageId,
                MessageParams.MARKDOWN, true
        ));
    }

    protected List<Validable> getAudioMessage(Long chatId, String message, File audioMessage) {
        return createMessage(MessageType.AUDIO, Map.of(
                MessageParams.CHAT_ID, chatId,
                MessageParams.AUDIO, audioMessage,
                MessageParams.CAPTION, message,
                MessageParams.MARKDOWN, true
        ));
    }

    protected String getAudioMessage(List<Validable> validables) {
        return validables.stream()
                .map(validable -> switch (validable) {
                            case SendMessage sendMessage -> sendMessage.getText();
                            case InputMedia inputMedia -> inputMedia.getCaption();
                            case SendAnimation sendAnimation -> sendAnimation.getCaption();
                            case SendPhoto sendPhoto -> sendPhoto.getCaption();
                            case SendVideo sendVideo -> sendVideo.getCaption();
                            case null, default -> "";
                        }
                )
                .collect(Collectors.joining());
    }
}
