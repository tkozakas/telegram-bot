package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.UnifiedMessageBuilder;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.*;
import org.churk.telegrambot.service.DailyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public abstract class ResponseHandler implements CommandHandler {
    @Autowired
    protected BotProperties botProperties;
    @Autowired
    protected DailyMessageService dailyMessageService;
    @Autowired
    protected UnifiedMessageBuilder unifiedMessageBuilder;

    protected MessageContext.MessageContextBuilder createMessageContextBuilder(UpdateContext context) {
        MessageContext.MessageContextBuilder builder = MessageContext.builder()
                .chatId(context.getUpdate().getMessage().getChatId())
                .isMarkdown(context.isMarkdown())
                .replyToMessageId(context.getUpdate().getMessage().getMessageId());

        if (context.isReply()) {
            builder.isReply(true);
        }

        return builder;
    }

    protected List<Validable> createMessage(MessageType messageType, Map<MessageParams, Object> params) {
        Map<MessageParams, Object> filteredParams = params.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return unifiedMessageBuilder.build(messageType, filteredParams);
    }

    protected List<Validable> createMessage(MessageType messageType, MessageContext context) {
        Map<MessageParams, Object> params = new HashMap<>();
        params.put(MessageParams.CHAT_ID, context.getChatId());
        params.put(MessageParams.TEXT, context.getText());
        if (context.isReply()) {
            params.put(MessageParams.REPLY_TO_MESSAGE_ID, context.getReplyToMessageId());
        }
        if (context.isMarkdown()) {
            params.put(MessageParams.MARKDOWN, true);
        }
        params.put(MessageParams.DOCUMENT, context.getDocument());
        params.put(MessageParams.STICKER, context.getSticker() != null ? context.getSticker().getFileId() : null);
        params.put(MessageParams.ANIMATION, context.getAnimation());
        params.put(MessageParams.CAPTION, context.getCaption());
        params.put(MessageParams.PHOTO, context.getPhotoUrl());
        params.put(MessageParams.VIDEO, context.getVideoUrl());
        params.put(MessageParams.MEDIA_GROUP, context.getMediaGroup());
        params.put(MessageParams.AUDIO, context.getAudioStream());

        return createMessage(messageType, params);
    }

    protected List<Validable> createTextMessage(UpdateContext context, String text) {
        return createMessage(MessageType.TEXT, createMessageContextBuilder(context)
                .text(text)
                .build());
    }

    protected List<Validable> createReplyMessage(UpdateContext context, String text) {
        return createMessage(MessageType.TEXT, createMessageContextBuilder(context)
                .text(text)
                .isReply(true)
                .build());
    }

    protected List<Validable> createDocumentMessage(UpdateContext context, File file) {
        return createMessage(MessageType.DOCUMENT, createMessageContextBuilder(context)
                .document(file)
                .build());
    }

    protected List<Validable> createStickerMessage(UpdateContext context, Sticker sticker) {
        return createMessage(MessageType.STICKER, createMessageContextBuilder(context)
                .sticker(sticker)
                .build());
    }

    protected List<Validable> createAnimationMessage(UpdateContext context, File file, String caption) {
        return createMessage(MessageType.ANIMATION, createMessageContextBuilder(context)
                .animation(file)
                .caption(caption)
                .build());
    }

    protected List<Validable> createPhotoMessage(UpdateContext context, String url, String caption) {
        return createMessage(MessageType.PHOTO, createMessageContextBuilder(context)
                .photoUrl(url)
                .caption(caption)
                .build());
    }

    protected List<Validable> createVideoMessage(UpdateContext context, String url, String caption) {
        return createMessage(MessageType.VIDEO, createMessageContextBuilder(context)
                .videoUrl(url)
                .caption(caption)
                .build());
    }

    protected List<Validable> createMediaGroupMessage(UpdateContext context, List<InputMedia> mediaGroup) {
        return createMessage(MessageType.MEDIA_GROUP, createMessageContextBuilder(context)
                .mediaGroup(mediaGroup)
                .build());
    }

    protected List<Validable> createAudioMessage(UpdateContext context, String caption, byte[] audioStream) {
        return createMessage(MessageType.AUDIO, createMessageContextBuilder(context)
                .caption(caption)
                .audioStream(audioStream)
                .build());
    }

    protected List<Validable> createLogMessage(UpdateContext context, String caption, String text) {
        File logFile = null;
        try {
            logFile = Files.createTempFile("log", ".txt").toFile();
            Files.writeString(logFile.toPath(), text + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Failed to write to log file: {}", logFile != null ? logFile.getPath() : "Unknown path", e);
        }
        return createMessage(MessageType.DOCUMENT, createMessageContextBuilder(context)
                .document(logFile)
                .caption(caption)
                .build());
    }

    protected String getAudioMessage(List<Validable> validables) {
        return validables.stream()
                .map(validable -> switch (validable) {
                    case SendMessage sendMessage -> sendMessage.getText();
                    case InputMedia inputMedia -> inputMedia.getCaption();
                    case SendAnimation sendAnimation -> sendAnimation.getCaption();
                    case SendPhoto sendPhoto -> sendPhoto.getCaption();
                    case SendVideo sendVideo -> sendVideo.getCaption();
                    default -> "";
                })
                .collect(Collectors.joining());
    }
}
